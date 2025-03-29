package com.ruokit.oilprice.controller

import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class MapViewController {

    @GetMapping("/map", produces = [MediaType.TEXT_HTML_VALUE])
    fun showMapPage(
        @RequestParam(required = false, defaultValue = "서울시 강서로56나길 110") address: String,
        @RequestParam(required = false, defaultValue = "2") radius: Int,
        model: Model
    ): String {
        model.addAttribute("address", address)
        model.addAttribute("radius", radius)
        return "map"
    }

}
