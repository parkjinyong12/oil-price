package com.ruokit.oilprice

import com.ruokit.oilprice.service.GPSConvertService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableCaching
class OilPriceBatchApplication : CommandLineRunner {

    override fun run(vararg args: String?) {
        val convert = GPSConvertService()
        val convertCoor1 = convert.convertWgs84ToKatec(127.830532, 38.300603)
        val convertCoor2 = convert.convertKatecToWgs84(245243.6181471178, 475950.9344670474)
        println(convertCoor1)
        println(convertCoor2)
    }
}

fun main(args: Array<String>) {
    runApplication<OilPriceBatchApplication>(*args)
}
