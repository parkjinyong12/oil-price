package com.ruokit.oilprice.opinet

data class OilStationResponse(
    val stationId: String,
    val stationName: String,
    val oilType: String,
    val price: Double,
    val x: Double,
    val y: Double
)
