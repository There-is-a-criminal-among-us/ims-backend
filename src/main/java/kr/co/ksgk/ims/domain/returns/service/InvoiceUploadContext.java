package kr.co.ksgk.ims.domain.returns.service;

import lombok.Getter;

import java.util.UUID;

/** Request-local diagnostics; retained by the controller through transaction commit. */
@Getter
public class InvoiceUploadContext {
    private final String errorId = UUID.randomUUID().toString();
    private String stage = "START";
    private String fileName;
    private Integer rowNumber;
    private String originalInvoice;
    private String returnInvoice;

    public void locate(String stage, String fileName, Integer rowNumber,
                       String originalInvoice, String returnInvoice) {
        this.stage = stage;
        this.fileName = fileName;
        this.rowNumber = rowNumber;
        this.originalInvoice = originalInvoice;
        this.returnInvoice = returnInvoice;
    }

    public void stage(String stage) {
        this.stage = stage;
    }

    public static String logValue(String value) {
        return value == null ? null : value.replaceAll("[\\p{Cntrl}\\u2028\\u2029]", "_");
    }
}
