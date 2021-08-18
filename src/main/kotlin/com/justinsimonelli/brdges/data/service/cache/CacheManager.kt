package com.justinsimonelli.brdges.data.service.cache

import com.justinsimonelli.brdges.data.service.mapper.SDOTResponseMapper
import com.justinsimonelli.brdges.data.service.models.BridgeStatus
import com.justinsimonelli.brdges.data.service.proxy.gov.SDOTProxy
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Component
@EnableScheduling
class CacheManager(private val sdotProxy: SDOTProxy,
                   private val sdotResponseMapper: SDOTResponseMapper
                   ) {

    private val bridgeStatuses = mutableMapOf<String, BridgeStatus>()
    private val lock = ReentrantLock()

    fun getBridgeStatuses(): Map<String, BridgeStatus> =
        bridgeStatuses.ifEmpty { refreshBridgeStatuses() }

    @Scheduled(fixedDelayString = "\${cache.bridges.refresh.intervalMillis}")
    private fun refreshBridgeStatuses(): Map<String, BridgeStatus> =
       lock.withLock {
           runBlocking {
               bridgeStatuses.clear()
               bridgeStatuses.plus(sdotResponseMapper.map(sdotProxy.pullBridgeData()))
           }
       }
}