package com.ruokit.oilprice.service

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import org.apache.tomcat.util.buf.Utf8Encoder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class GeocodingService(
    @Value("\${google.maps.api.key}")
    private val apiKey: String,
    @Value("\${google.maps.api.base-url}")
    private val url: String
) {
    private val restTemplate = RestTemplate()

    private val logger = LoggerFactory.getLogger(GeocodingService::class.java)

    @Cacheable("geocodeCache")
    fun getWgs84FromAddress(address: String): Pair<Double, Double> {

        logger.info("[API 호출] 주소 → 좌표 변환 시도: $address")
        val url = UriComponentsBuilder.fromHttpUrl(url)
            .queryParam("address", address)
            .queryParam("key", apiKey)
            .build()
            .toUriString()

        val response: JsonNode = restTemplate.getForObject(url, JsonNode::class.java)
            ?: throw IllegalStateException("Google API 응답 없음")

        val status = response["status"].asText()

        if (status == "REQUEST_DENIED") {
            val errorMessage = response["error_message"]
            throw IllegalStateException("Google API 요청 거부됨: $errorMessage")
        }

        val locationNode = response["results"]?.firstOrNull()?.get("geometry")?.get("location")
            ?: throw IllegalArgumentException("위치 정보를 찾을 수 없습니다")

        val lat = locationNode["lat"].asDouble()
        val lon = locationNode["lng"].asDouble()

        return Pair(lat, lon)
    }
}
