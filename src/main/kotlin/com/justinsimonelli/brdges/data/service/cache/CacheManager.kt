package com.justinsimonelli.brdges.data.service.cache

import com.justinsimonelli.brdges.data.service.Constants.ZONE_PST
import com.justinsimonelli.brdges.data.service.mapper.SDOTResponseMapper
import com.justinsimonelli.brdges.data.service.models.BridgeStatusResponse
import com.justinsimonelli.brdges.data.service.proxy.gov.SDOTProxy
import com.justinsimonelli.brdges.data.service.proxy.gov.SpoofSDOTProxy
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Component
@EnableScheduling
class CacheManager(
    private val sdotProxy: SDOTProxy,
    private val spoofSDOTProxy: SpoofSDOTProxy,
    private val sdotResponseMapper: SDOTResponseMapper,
    private val dateFormatter: DateTimeFormatter
) {

    private lateinit var bridgeStatusesCache: BridgeStatusResponse

    private val lock = ReentrantLock()

    init {
        refreshBridgeStatusesCache()
    }

    fun bridgeStatuses(force: Boolean?, spoofName: String?): BridgeStatusResponse {
        return if (force == true) {
            pullLatestBridgeStatuses(spoofName = spoofName)
        } else {
            bridgeStatusesCache
        }
    }

    @Scheduled(fixedDelayString = "\${cache.bridges.refresh.intervalMillis}")
    private fun refreshBridgeStatusesCache() {
        bridgeStatusesCache = pullLatestBridgeStatuses()
    }

    private fun pullLatestBridgeStatuses(spoofName: String? = null): BridgeStatusResponse {
        return lock.withLock {
            runBlocking {
                val bridgeData = if (spoofName.isNullOrEmpty()) {
                    sdotProxy.pullBridgeData()
                } else {
                    spoofSDOTProxy.pullBridgeData(spoofName = spoofName)
                }
                BridgeStatusResponse(
                    lastUpdated = dateFormatter.format(ZonedDateTime.now(ZoneId.of(ZONE_PST, ZoneId.SHORT_IDS))),
                    statuses = sdotResponseMapper.map(bridgeData)
                )
            }
        }
    }
}