package com.ruokit.oilprice.controller

import com.ruokit.oilprice.kakao.KakaoRequest
import com.ruokit.oilprice.kakao.KakaoResponse
import com.ruokit.oilprice.service.GasStationQueryService
import com.ruokit.oilprice.service.OilPriceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class KakaoWebhookController(
    private val gasStationQueryService: GasStationQueryService
) {

    @PostMapping("/webhook")
    fun kakaoWebhook(@RequestBody request: KakaoRequest): ResponseEntity<KakaoResponse> {
        val address = request.userRequest.utterance

        // 주소 기반으로 주유소 조회
        val stations = gasStationQueryService.findNearbyStations(address, 2.0)

        if (stations.isEmpty()) {
            return ResponseEntity.ok(
                simpleText("❗ 주유소 정보를 찾을 수 없어요. 주소를 다시 확인해 주세요.")
            )
        }

        // 최저가 기준 정렬 후 상위 5개만 선택
        val topStations = stations.sortedBy { it.price }.take(5)

        val stationListText = topStations.joinToString("\n") { station ->
            "⛽ ${station.stationName} - ${station.price}원"
        }

        return ResponseEntity.ok(
            simpleText("📍 [$address] 인근 최저가 주유소\n\n$stationListText")
        )
    }

    private fun simpleText(text: String): Map<String, Any> {
        return mapOf(
            "version" to "2.0",
            "template" to mapOf(
                "outputs" to listOf(
                    mapOf("simpleText" to mapOf("text" to text))
                )
            )
        )
    }
}