package kr.co.ksgk.ims.domain.ocr.dto.response;

public record ExtractedInvoice(
        String invoice_number,
        String sender_name,
        String sender_phone,
        String item_name
) {
}
