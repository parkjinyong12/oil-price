package com.ruokit.oilprice.opinet

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.hibernate.query.results.Builders.entity
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder


@Component
class OpinetClientImpl(
    @Value("\${opinet.api.key}") private val apiKey: String,
    @Value("\${opinet.api.base-url}") private val baseUrl: String
) : OpinetClient {

    private val restTemplate = RestTemplate()
    override fun fetchAllStations(): List<OilStationResponse> {
        TODO("Not yet implemented")
    }

    override fun fetchStationsAround(x: Int, y: Int, radius: Int): List<OilStationResponse> {
        // 여러 유종을 한번에 조회하기 위해 prodcd 파라미터에 여러 값을 전달
        val fuelTypes = listOf("B027", "B028", "B029")  // 보통휘발유, 고급휘발유, 경유

        println("x: $x, y: $y, radius: $radius")
        // prodcd 파라미터에 여러 값을 한 번에 전달
        val url = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .queryParam("code", apiKey)
            .queryParam("out", "json")
            .queryParam("x", x)
            .queryParam("y", y)
            .queryParam("radius", radius)
            .queryParam("sort", "2") // 거리순
            .queryParam("prodcd", "B027") // 여러 유종 코드 (콤마로 구분)
            .queryParam("out", "json")
            .build()
            .toUriString()

        try {
            // 실제 API 호출
            val restTemplate = RestTemplate()

            val response: String = restTemplate.getForObject(url, String::class.java) ?: ""

            // 2. Jackson으로 모델로 파싱
            val mapper = jacksonObjectMapper()
            val parsed: OpinetRawResponse = mapper.readValue(response, OpinetRawResponse::class.java)

            // 응답이 비어있다면 빈 리스트 반환
            if (parsed.result.oilList.isEmpty()) {
                println("API 호출은 성공했지만 데이터가 없습니다.")
                return emptyList()
            }

            parsed.result.oilList.forEach { oilStation ->
                println("주유소 이름: ${oilStation.stationName}, 휘발유 가격: ${oilStation.price}")
            }
            return parsed.result.oilList.map {
                OilStationResponse(
                    stationId = it.stationCode,
                    stationName = it.stationName,
                    oilType = "B027",
                    price = it.price,
                    x = it.x,
                    y = it.y
                )
            }
        } catch (e: HttpClientErrorException) {
            // 4xx 응답 처리 (클라이언트 오류)
            println("API 호출 실패: Client error - ${e.statusCode}")
        } catch (e: HttpServerErrorException) {
            // 5xx 응답 처리 (서버 오류)
            println("API 호출 실패: Server error - ${e.statusCode}")
        } catch (e: Exception) {
            // 기타 예외 처리
            println("API 호출 실패: ${e.message}")
        }

        return emptyList()  // 실패한 경우 빈 리스트 반환
    }
}
