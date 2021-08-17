package com.justinsimonelli.brdges.data.service

import com.justinsimonelli.brdges.data.service.cache.CacheManager
import com.justinsimonelli.brdges.data.service.models.AvailableBridge
import com.justinsimonelli.brdges.data.service.models.gov.BridgeData
import com.justinsimonelli.brdges.data.service.models.BridgeStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class RestController(
    private val cacheManager: CacheManager
) {

    @GetMapping("/health")
    fun health(): ResponseEntity<String> =
        ResponseEntity.ok("Healthy")

    @GetMapping("/available-bridges")
    fun availableBridges(): ResponseEntity<Map<String, AvailableBridge>> =
        ResponseEntity.ok(cacheManager.getAvailableBridges())

    @GetMapping("/statuses")
    fun getBridgeStatuses(): ResponseEntity<Map<String, BridgeStatus>> =
        ResponseEntity.ok(cacheManager.getBridgeStatuses())

    @GetMapping("/sdot-statuses")
    fun getSdotBridgeStatuses(): ResponseEntity<Map<String, BridgeStatus>> =
        ResponseEntity.ok(cacheManager.getBridgeData())
}