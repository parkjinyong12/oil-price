package com.ruokit.oilprice.opinet

import com.fasterxml.jackson.annotation.JsonProperty

data class OpinetRawResponse(
    @JsonProperty("RESULT")
    val result: OpinetResult
)

data class OpinetResult(
    @JsonProperty("OIL")
    val oilList: List<OpinetOilStation> = emptyList()
)

data class OpinetOilStation(
    @JsonProperty("UNI_ID")
    val stationCode: String,

    @JsonProperty("POLL_DIV_CD")
    val stationBrand: String,

    @JsonProperty("OS_NM")
    val stationName: String,

    @JsonProperty("PRICE")
    val price: Double,

    @JsonProperty("DISTANCE")
    val distance: Double,

    @JsonProperty("GIS_X_COOR")
    val x: Double,

    @JsonProperty("GIS_Y_COOR")
    val y: Double
)