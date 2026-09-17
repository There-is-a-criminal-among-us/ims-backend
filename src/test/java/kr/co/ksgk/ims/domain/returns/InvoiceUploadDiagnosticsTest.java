package kr.co.ksgk.ims.domain.returns;

import kr.co.ksgk.ims.domain.invoice.repository.InvoiceRepository;
import kr.co.ksgk.ims.domain.member.repository.*;
import kr.co.ksgk.ims.domain.returns.controller.ReturnController;
import kr.co.ksgk.ims.domain.returns.dto.response.*;
import kr.co.ksgk.ims.domain.returns.entity.ReturnInfo;
import kr.co.ksgk.ims.domain.returns.exception.InvoiceValidationException;
import kr.co.ksgk.ims.domain.returns.repository.*;
import kr.co.ksgk.ims.domain.returns.service.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.TransactionSystemException;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class InvoiceUploadDiagnosticsTest {
    private final ReturnInfoRepository returns = mock(ReturnInfoRepository.class);
    private final InvoiceRepository invoices = mock(InvoiceRepository.class);
    private final ReturnService service = new ReturnService(mock(ReturnHandlerRepository.class),
            mock(ReturnMallRepository.class), returns, mock(MemberRepository.class),
            mock(MemberBrandRepository.class), mock(MemberCompanyRepository.class), invoices);

    private MockMultipartFile workbook(String... returnNumbers) throws Exception {
        try (var workbook = new XSSFWorkbook(); var bytes = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet();
            var header = sheet.createRow(0);
            header.createCell(0).setCellValue("원송장번호");
            header.createCell(1).setCellValue("반송장번호");
            for (int i = 0; i < returnNumbers.length; i++) {
                var row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue("123-456");
                row.createCell(1).setCellValue(returnNumbers[i]);
            }
            workbook.write(bytes);
            return new MockMultipartFile("files", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes.toByteArray());
        }
    }

    @Test
    void recordsOriginalInvoiceLookupFailureAtExcelRow() throws Exception {
        when(returns.findByNormalizedOriginalInvoice("123456"))
                .thenThrow(new IncorrectResultSizeDataAccessException(1, 2));
        var context = new InvoiceUploadContext();
        var file = workbook("999-111");
        assertThatThrownBy(() -> service.uploadReturnInvoices(List.of(file), context))
                .isInstanceOf(IncorrectResultSizeDataAccessException.class);
        assertThat(context.getStage()).isEqualTo("LOOKUP_ORIGINAL_INVOICE");
        assertThat(context.getFileName()).isEqualTo("test.xlsx");
        assertThat(context.getRowNumber()).isEqualTo(2);
        assertThat(context.getOriginalInvoice()).isEqualTo("123456");
        assertThat(context.getReturnInvoice()).isEqualTo("999111");
    }

    @Test
    void retainsLastMappingSourceRowWhenReturnInvoiceLookupFails() throws Exception {
        when(returns.findByNormalizedOriginalInvoice("123456"))
                .thenReturn(Optional.of(ReturnInfo.builder().build()));
        when(invoices.findByNormalizedNumber("999222"))
                .thenThrow(new IncorrectResultSizeDataAccessException(1, 2));
        var response = new ReturnController(service).uploadReturnInvoices(List.of(workbook("999-111", "999-222")));
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        var body = (InvoiceUploadFailureResponse) response.getBody();
        assertThat(body.errorType()).isEqualTo("NON_UNIQUE_INVOICE");
        assertThat(body.stage()).isEqualTo("LOOKUP_RETURN_INVOICE");
        assertThat(body.rowNumber()).isEqualTo(3);
        assertThat(body.fileName()).isEqualTo("test.xlsx");
        assertThat(body.errorId()).matches("[0-9a-f-]{36}");
        verify(invoices, never()).findByNormalizedNumber("999111");
    }

    @Test
    void commitFailureHasRequestIdButDoesNotBlameLastRowOrExposeException() {
        var mockedService = mock(ReturnService.class);
        when(mockedService.uploadReturnInvoices(anyList(), any())).thenAnswer(invocation -> {
            InvoiceUploadContext context = invocation.getArgument(1);
            context.locate("TRANSACTION_COMMIT", null, null, null, null);
            throw new TransactionSystemException("secret database connection details");
        });
        var response = new ReturnController(mockedService).uploadReturnInvoices(List.of());
        var body = (InvoiceUploadFailureResponse) response.getBody();
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(body.stage()).isEqualTo("TRANSACTION_COMMIT");
        assertThat(body.rowNumber()).isNull();
        assertThat(body.fileName()).isNull();
        assertThat(body.message()).doesNotContain("secret");
        assertThat(body.errorId()).isNotBlank();
    }

    @Test
    void emptyResultIsNotMisreportedAsDuplicateInvoice() {
        var mockedService = mock(ReturnService.class);
        when(mockedService.uploadReturnInvoices(anyList(), any()))
                .thenThrow(new org.springframework.dao.EmptyResultDataAccessException(1));
        var response = new ReturnController(mockedService).uploadReturnInvoices(List.of());
        var body = (InvoiceUploadFailureResponse) response.getBody();
        assertThat(body.errorType()).isEqualTo("INTERNAL_ERROR");
    }

    @Test
    void preservesExistingValidationResponse() {
        var mockedService = mock(ReturnService.class);
        var error = InvoiceUploadErrorResponse.of(List.of());
        when(mockedService.uploadReturnInvoices(anyList(), any())).thenThrow(new InvoiceValidationException(error));
        var response = new ReturnController(mockedService).uploadReturnInvoices(List.of());
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isSameAs(error);
    }

    @Test
    void successfulUploadLeavesCommitStageAndExistingSuccessResponse() throws Exception {
        when(returns.findByNormalizedOriginalInvoice("123456")).thenReturn(Optional.empty());
        var context = new InvoiceUploadContext();
        var response = service.uploadReturnInvoices(List.of(workbook("999111")), context);
        assertThat(response.notFoundInvoices()).containsExactly("123456");
        assertThat(response.totalFiles()).isEqualTo(1);
        assertThat(context.getStage()).isEqualTo("TRANSACTION_COMMIT");
        assertThat(context.getRowNumber()).isNull();
    }
}
