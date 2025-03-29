package com.ruokit.oilprice.controller

import com.ruokit.oilprice.domain.OilStation
import com.ruokit.oilprice.service.GasStationQueryService
import com.ruokit.oilprice.service.OilPriceService
import com.ruokit.oilprice.service.OpinetGridBatchService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class OpinetGridBatchController(
    private val gasStationQueryService: GasStationQueryService,
    private val oilPriceService: OilPriceService
) {

    private val logger = LoggerFactory.getLogger(OpinetGridBatchService::class.java)
    @Autowired
    private lateinit var opinetGridBatchService: OpinetGridBatchService

    /**
     * HTTP GET 요청으로 배치 작업 실행
     */
    @GetMapping("/batch")
    fun runBatch(): String {
        opinetGridBatchService.runBatch()
        return "배치 작업이 성공적으로 실행되었습니다!"
    }

    @GetMapping("/batch-test")
    fun runTest(): String {
        val gridSize = 5000 // API 최대 반경 5km = 5,000m
        val x = 314681
        val y = 544837
//        val x = 289484
//        val y = 563693
//        oilPriceService.fetchStationsAround(x, y, gridSize)
        oilPriceService.saveStationsAround(x, y, gridSize)
        return "배치 작업이 성공적으로 실행되었습니다!"
    }

    @GetMapping("/nearby")
    fun getNearbyStations(
        @RequestParam address: String,
        @RequestParam(required = false, defaultValue = "2") radius: Int
    ): ResponseEntity<List<OilStation>> {
        logger.info("address: $address")
        if (radius !in 0..5) {
            return ResponseEntity.badRequest().body(emptyList())
        }
        val result = gasStationQueryService.findNearbyStations(address, radius.toDouble())
        return ResponseEntity.ok(result)
    }
}
