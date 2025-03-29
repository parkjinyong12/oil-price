package com.ruokit.oilprice.service

import com.ruokit.oilprice.domain.UserGasStation
import com.ruokit.oilprice.domain.OilStation
import com.ruokit.oilprice.opinet.OilStationResponse
import com.ruokit.oilprice.repository.UserGasStationRepository
import com.ruokit.oilprice.repository.UserRepository
import com.ruokit.oilprice.repository.OilStationRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.*

@Service
class OilPriceBatchService(
    private val userRepository: UserRepository,
    private val userGasStationRepository: UserGasStationRepository,
    private val oilStationRepository: OilStationRepository
) {

    /**
     * 오피넷에서 전체 주유소 정보를 수집하고 저장 (중복 방지)
     * @param fetch 전체 주유소 데이터를 반환하는 함수
     */
    fun collectAllStations(entities: List<OilStation>) {
        oilStationRepository.saveAll(entities)
    }

    /**
     * 사용자 위치를 기준으로 저장된 주유소들 중 반경 2km 이내 데이터를 필터링하고 저장
     */
    fun assignNearbyStationsToUsers() {
        val users = userRepository.findAll()
        val allStations = oilStationRepository.findAll()

        for (user in users) {
            val filtered = allStations.filter {
                calculateDistance(user.lat, user.lon, it.wgs84Y, it.wgs84X) <= 2.0
            }

            val entities = filtered.map {
                UserGasStation(
                    user = user,
                    stationName = it.stationName,
                    oilType = it.oilType,
                    price = it.price,
                    distance = calculateDistance(user.lat, user.lon, it.wgs84Y, it.wgs84X),
                    wgs84X = it.wgs84X,
                    wgs84Y = it.wgs84Y,
                    address = "",
                    collectedAt = LocalDate.now()
                )
            }

            userGasStationRepository.saveAll(entities)
        }
    }

    /**
     * 두 좌표 간의 거리를 계산 (Haversine 공식)
     * @return km 단위 거리
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // 지구 반지름 (단위: km)
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c // 거리 (km)
    }
}
