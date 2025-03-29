package com.ruokit.oilprice.service

import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.CoordinateTransform
import org.locationtech.proj4j.CoordinateTransformFactory
import org.locationtech.proj4j.ProjCoordinate

class GPSConvertService {

    private val crsFactory = CRSFactory()
    private val ctFactory = CoordinateTransformFactory()

    private val wgs84CRS = crsFactory.createFromParameters(
        "WGS84",
        "+proj=latlong +datum=WGS84 +ellps=WGS84"
    )

    private val katecCRS = crsFactory.createFromParameters(
        "KATEC",
        "+proj=tmerc +lat_0=38N +lon_0=128E +ellps=bessel +x_0=400000 +y_0=600000 +k=0.9999 +units=m +towgs84=-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43"
    )

    private val toWgs84Transform: CoordinateTransform = ctFactory.createTransform(katecCRS, wgs84CRS)
    private val toKatecTransform: CoordinateTransform = ctFactory.createTransform(wgs84CRS, katecCRS)

    fun convertKatecToWgs84(x: Double, y: Double): Pair<Double, Double> {

        val srcCoord = ProjCoordinate(x, y)
        val destCoord = ProjCoordinate()

        toWgs84Transform.transform(srcCoord, destCoord)
        return Pair(destCoord.x, destCoord.y)
    }

    fun convertWgs84ToKatec(x: Double, y: Double): Pair<Double, Double> {

        // 위도, 경도 -> GIS 에서 사용될 때는 경도, 위도
        val srcCoord = ProjCoordinate(x, y)  // (lon, lat)
        val destCoord = ProjCoordinate()

        toKatecTransform.transform(srcCoord, destCoord)
        return Pair(destCoord.x, destCoord.y)
    }
}
