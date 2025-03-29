package com.ruokit.oilprice.service

import com.ruokit.oilprice.domain.OilStation
import com.ruokit.oilprice.opinet.OilStationResponse
import com.ruokit.oilprice.opinet.OpinetClient
import com.ruokit.oilprice.repository.OilStationRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class OilPriceService(
    private val opinetClient: OpinetClient,
    private val oilStationRepository: OilStationRepository
) {
    fun getStationsAround(x: Int, y: Int, radius: Int): List<OilStation> {
        val gpsConvertService = GPSConvertService()
        // 오피넷 호출
        return opinetClient.fetchStationsAround(x, y, radius).map { oilStation ->
            gpsConvertService.convertKatecToWgs84(oilStation.x, oilStation.y).let { wgs84 ->
                OilStation(
                    stationId = oilStation.stationId,
                    stationName = oilStation.stationName,
                    oilType = oilStation.oilType,
                    price = oilStation.price,
                    katecX = oilStation.x,
                    katecY = oilStation.y,
                    wgs84X = wgs84.first,
                    wgs84Y = wgs84.second,
                    collectedDt = LocalDateTime.now()
                )
            }
        }
    }

    fun saveStationsAround(x: Int, y: Int, radius: Int) {
        val stations = getStationsAround(x, y, radius)
        oilStationRepository.saveAll(stations)
    }
}