package com.ruokit.oilprice.service

import com.ruokit.oilprice.batch.OpinetGridBatch
import com.ruokit.oilprice.opinet.OilStationResponse
import com.ruokit.oilprice.opinet.OpinetClient
import org.springframework.stereotype.Service

@Service
class OpinetGridBatchService(
    private val opinetGridBatch: OpinetGridBatch
) {

    /**
     * 주유소 수집 배치를 서비스로 실행
     */
    fun runBatch() {
        println("[START] 오피넷 전국 주유소 수집 배치 실행")
        opinetGridBatch.run()  // OpinetGridBatch의 실제 배치 작업 실행
        println("[END] 오피넷 전국 주유소 수집 배치 완료")
    }
}
