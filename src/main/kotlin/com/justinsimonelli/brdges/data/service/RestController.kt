package com.justinsimonelli.brdges.data.service

import com.justinsimonelli.brdges.data.service.cache.CacheManager
import com.justinsimonelli.brdges.data.service.models.BridgeStatusResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.inject.Inject

@Controller
class RestController
@Inject
constructor(
    private val cacheManager: CacheManager,
    @Value("\${app.version}") private val appVersion: String){

    @GetMapping("/")
    fun version() = ResponseEntity.ok("Running v${appVersion}")

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