package com.ruokit.oilprice.service

import com.ruokit.oilprice.domain.OilStation
import com.ruokit.oilprice.repository.OilStationRepository
import org.springframework.stereotype.Service
import kotlin.math.*

@Service
class GasStationQueryService(
    private val oilStationRepository: OilStationRepository,
    private val geocodingService: GeocodingService
) {
    fun findNearbyStations(address: String, radiusKm: Double): List<OilStation> {
        val (userLat, userLon) = geocodingService.getWgs84FromAddress(address)
        println("km: $radiusKm, Lat: $userLat, Lon: $userLon")
        return oilStationRepository.findLatestStations()
            .filter {
                val distance = haversine(userLat, userLon, it.wgs84Y, it.wgs84X)
                distance <= radiusKm
            }
            .sortedBy { it.price }
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c  // km
    }
}
