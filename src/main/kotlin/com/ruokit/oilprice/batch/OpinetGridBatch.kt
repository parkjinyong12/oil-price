package com.ruokit.oilprice.batch

import com.ruokit.oilprice.domain.OilStation
import com.ruokit.oilprice.repository.OilStationRepository
import com.ruokit.oilprice.service.GeocodingService
import com.ruokit.oilprice.service.OilPriceService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Component
class OpinetGridBatch(
    private val oilStationRepository: OilStationRepository
) {
    @Autowired
    private lateinit var oilPriceService: OilPriceService

    private val logger = LoggerFactory.getLogger(OpinetGridBatch::class.java)

    /**
     * 전국을 일정 간격으로 나눈 좌표 그리드 기반으로 전체 주유소 정보를 수집한다.
     */
    fun run() {

        val batchId = UUID.randomUUID().toString()
        // 경기권 전체 커버
//        val startX = 248175  // 서쪽 끝 (경기 파주시, 김포시 방향)
//        val endX = 385086    // 동쪽 끝 (경기 가평군, 양평군 방향)
//        val startY = 634485  // 남쪽 끝 (경기 평택시, 안성시 방향)
//        val endY = 474556    // 북쪽 끝 (경기 연천군, 포천시 포함)

        // 서울 전체 커버
        // (126.262021, 36.87226) -> 굴항1길 충청남도 태안군 이원면 -> (245243.6181471178, 475950.9344670474)
        // (127.830532, 38.300603) -> 임남면 강원특별자치도 철원군 -> (385369.9905603671, 633071.5507245103)
        val gridSize = 5000 // API 최대 반경 5km = 5,000m
        val startX = 245243  // 서쪽 끝 (경기 파주시, 김포시 방향)
        val endX = 385369    // 동쪽 끝 (경기 가평군, 양평군 방향)
        val startY = 475950  // 남쪽 끝 (경기 평택시, 안성시 방향)
        val endY = 633071    // 북쪽 끝 (경기 연천군, 포천시 포함)

        // 테스트용
//        val gridSize = 2000
//        val startX = 312681
//        val endX = 316681
//        val startY = 542837
//        val endY = 546837

        val oilStations = mutableListOf<OilStation>()

        var count = 0
        for (x in startX..endX step gridSize) {
            for (y in startY..endY step gridSize) {

                try {
                    oilStations.addAll(oilPriceService.getStationsAround(x, y, gridSize))

                    count += 1
                    logger.info("call count: $count")
                    // ✅ 과도한 호출 방지 (API 부하 고려)
                    Thread.sleep(1000L)

                } catch (e: Exception) {
                    logger.error("❗ API 호출 오류: x=$x, y=$y - ${e.message}")
                    continue
                }
            }
        }
        val sorted = oilStations
            .associateBy { it.stationId }  // stationId 기준으로 덮어쓰기 → 중복 제거
            .values.onEach { oilStation ->
                oilStation.batchId = batchId
            }.toList()

        oilStationRepository.saveAll(sorted)
    }
}
