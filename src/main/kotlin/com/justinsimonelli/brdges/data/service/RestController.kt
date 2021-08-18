package com.justinsimonelli.brdges.data.service

import com.justinsimonelli.brdges.data.service.cache.CacheManager
import com.justinsimonelli.brdges.data.service.models.BridgeStatusResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class RestController(
    private val cacheManager: CacheManager
) {

    @GetMapping("/health")
    fun health(): ResponseEntity<String> =
        ResponseEntity.ok("Healthy")

    @GetMapping("/statuses")
    fun getBridgeStatuses(
        @RequestParam force: Boolean?,
        @RequestParam spoofName: String?
    ): ResponseEntity<BridgeStatusResponse> =
        ResponseEntity.ok(cacheManager.bridgeStatuses(force, spoofName))
}