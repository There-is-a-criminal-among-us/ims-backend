package kr.co.ksgk.ims.domain.returns.service;

import kr.co.ksgk.ims.domain.invoice.repository.InvoiceRepository;
import kr.co.ksgk.ims.domain.returns.entity.ReturnInfo;
import kr.co.ksgk.ims.domain.returns.entity.ReturnStatus;
import kr.co.ksgk.ims.domain.returns.repository.ReturnInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReturnStatusScheduler {

    private final ReturnInfoRepository returnInfoRepository;
    private final InvoiceRepository invoiceRepository;

    @Scheduled(cron = "0 30 0 * * ?")
    @Transactional
    public void syncReturnStatuses() {
        log.info("회수 상태 동기화 시작");

        List<ReturnInfo> pending = returnInfoRepository.findAll().stream()
                .filter(r -> r.getReturnInvoice() != null && !r.getReturnInvoice().isBlank())
                .filter(r -> r.getReturnStatus() != ReturnStatus.COMPLETED)
                .toList();

        int count = 0;
        for (ReturnInfo r : pending) {
            String normalized = r.getReturnInvoice().replaceAll("-", "").trim();
            if (invoiceRepository.findByNormalizedNumber(normalized).isPresent()) {
                r.complete();
                count++;
            }
        }

        log.info("회수 상태 동기화 완료: {}건 처리", count);
    }
}
