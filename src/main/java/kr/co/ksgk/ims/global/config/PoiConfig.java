package kr.co.ksgk.ims.global.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.util.IOUtils;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class PoiConfig {

    private static final int MAX_BYTE_ARRAY_OVERRIDE = 300_000_000;
    private static final long MAX_ZIP_ENTRY_SIZE = 300_000_000L;

    @PostConstruct
    void configurePoiLimits() {
        IOUtils.setByteArrayMaxOverride(MAX_BYTE_ARRAY_OVERRIDE);
        ZipSecureFile.setMaxEntrySize(MAX_ZIP_ENTRY_SIZE);
        log.info("POI limits set: byteArrayMaxOverride={}, zipMaxEntrySize={}",
                MAX_BYTE_ARRAY_OVERRIDE, MAX_ZIP_ENTRY_SIZE);
    }
}
