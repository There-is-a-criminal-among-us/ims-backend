package kr.co.ksgk.ims.domain.ocr.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.ksgk.ims.domain.S3.service.S3Service;
import kr.co.ksgk.ims.domain.ocr.dto.request.OpenAiChatMessage;
import kr.co.ksgk.ims.domain.ocr.dto.request.OpenAiFunctionCallRequest;
import kr.co.ksgk.ims.domain.ocr.dto.request.NaverOcrRequest;
import kr.co.ksgk.ims.domain.ocr.dto.request.OcrExtractRequest;
import kr.co.ksgk.ims.domain.ocr.dto.response.ExtractedInvoice;
import kr.co.ksgk.ims.domain.ocr.dto.response.OcrExtractResponse;
import kr.co.ksgk.ims.domain.ocr.dto.response.OpenAiResponse;
import kr.co.ksgk.ims.domain.product.entity.ProductMapping;
import kr.co.ksgk.ims.domain.product.entity.Product;
import kr.co.ksgk.ims.domain.product.repository.ProductMappingRepository;
import kr.co.ksgk.ims.domain.product.repository.ProductRepository;
import kr.co.ksgk.ims.global.error.exception.BusinessException;
import kr.co.ksgk.ims.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OcrService {

    private final S3Service s3Service;
    private final WebClient webClient;
    private final ProductRepository productRepository;
    private final ProductMappingRepository productMappingRepository;

    @Value("${naver.api.url}")
    private String naverOcrApiUrl;

    @Value("${naver.api.key}")
    private String naverOcrApiKey;

    @Value("${openai.api.url}")
    private String openaiApiUrl;

    @Value("${openai.api.key}")
    private String openaiApiKey;


    public OcrExtractResponse extractInvoice(OcrExtractRequest request) throws JsonProcessingException {
        String invoiceImageUrl = s3Service.generateStaticUrl(request.invoiceKeyName());
        String ocrText = extractTextWithNaverOcr(invoiceImageUrl);
        ExtractedInvoice extractedInvoice = extractFromOcr(ocrText);
        List<ProductMapping> productMappings = productMappingRepository.findProductsByRawName(extractedInvoice.item_name());
        List<Product> products = productMappings.stream()
                .map(ProductMapping::getProduct)
                .collect(Collectors.toList());
        return OcrExtractResponse.of(extractedInvoice, products, invoiceImageUrl);
    }

    private String extractTextWithNaverOcr(String imageUrl) {
        NaverOcrRequest body = NaverOcrRequest.builder()
                .version("V2")
                .requestId(UUID.randomUUID().toString())
                .timestamp(Long.valueOf(System.currentTimeMillis()).intValue())
                .images(
                        List.of(NaverOcrRequest.ImageInfo.builder()
                                .format("png")
                                .name("invoice")
                                .url(imageUrl)
                                .build())
                )
                .build();

        try {
            return webClient.post()
                    .uri(naverOcrApiUrl)
                    .header("X-OCR-SECRET", naverOcrApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new BusinessException("Naver OCR API 호출 실패: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new BusinessException("OCR 텍스트 추출 중 오류 발생", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private ExtractedInvoice extractFromOcr(String ocrText) throws JsonProcessingException {
        OpenAiChatMessage system = OpenAiChatMessage.builder()
                .role("system")
                .content("당신은 OCR 결과에서 운송장번호, 보내는분 이름·전화번호, 상품명을 정확히 추출하는 전문가입니다.")
                .build();
        OpenAiChatMessage user = OpenAiChatMessage.builder()
                .role("user")
                .content("다음은 OCR 텍스트입니다:\n\n" + ocrText)
                .build();

        Map<String, Object> params = Map.of(
                "type", "object",
                "properties", Map.of(
                        "invoice_number", Map.of("type", "string", "description", "XXXX-XXXX-XXXX 형태의 운송장번호"),
                        "sender_name", Map.of("type", "string", "description", "보내는분 이름"),
                        "sender_phone", Map.of("type", "string", "description", "보내는분 전화번호"),
                        "item_name", Map.of("type", "string", "description", "송장에 기재된 실제 상품명")
                ),
                "required", List.of("invoice_number", "sender_name", "sender_phone", "item_name")
        );

        OpenAiFunctionCallRequest.FunctionDef function = OpenAiFunctionCallRequest.FunctionDef.builder()
                .name("extract_invoice_data")
                .description("송장 OCR 결과에서 주요 정보를 JSON으로 반환합니다.")
                .parameters(params)
                .build();

        OpenAiFunctionCallRequest requestBody = OpenAiFunctionCallRequest.builder()
                .model("gpt-4")
                .temperature(0)
                .messages(List.of(system, user))
                .functions(List.of(function))
                .function_call(Map.of("name", "extract_invoice_data"))
                .build();

        OpenAiResponse response;
        try {
            response = webClient.post()
                    .uri(openaiApiUrl)
                    .header("Authorization", "Bearer " + openaiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(OpenAiResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new BusinessException("OpenAI API 호출 실패: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new BusinessException("AI 데이터 추출 중 오류 발생", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        String jsonArgs = response.choices().get(0).message().function_call().arguments();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonArgs, ExtractedInvoice.class);
    }
}
