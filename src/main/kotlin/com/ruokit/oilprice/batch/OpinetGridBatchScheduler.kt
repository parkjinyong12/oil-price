package com.ruokit.oilpricebatch.scheduler

import com.ruokit.oilprice.batch.OpinetGridBatch
import com.ruokit.oilprice.service.OpinetGridBatchService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class OpinetGridBatchScheduler(
    private val opinetGridBatchService: OpinetGridBatchService
) {

    private val logger = LoggerFactory.getLogger(OpinetGridBatchScheduler::class.java)

    /**
     * 매일 새벽 3시에 배치 실행
     */
    @Scheduled(cron = "0 0 3 * * *")
    fun scheduleFixedRateTask() {
        logger.info("[START] 오피넷 전국 주유소 수집 배치 실행")
        opinetGridBatchService.runBatch()  // 서비스에서 배치 실행
        logger.info("[END] 오피넷 전국 주유소 수집 배치 완료")
    }
}
