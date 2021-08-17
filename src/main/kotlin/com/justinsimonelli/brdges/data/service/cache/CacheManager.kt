package com.justinsimonelli.brdges.data.service.cache

import com.justinsimonelli.brdges.data.service.mapper.SDOTResponseMapper
import com.justinsimonelli.brdges.data.service.mapper.TwitterResponseMapper
import com.justinsimonelli.brdges.data.service.models.AvailableBridge
import com.justinsimonelli.brdges.data.service.models.BridgeStatus
import com.justinsimonelli.brdges.data.service.models.gov.BridgeData
import com.justinsimonelli.brdges.data.service.proxy.amazon.S3Proxy
import com.justinsimonelli.brdges.data.service.proxy.gov.SDOTProxy
import com.justinsimonelli.brdges.data.service.proxy.twitter.TwitterProxy
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@EnableScheduling
class CacheManager(private val s3Proxy: S3Proxy,
                   private val twitterProxy: TwitterProxy,
                   private val sdotProxy: SDOTProxy,
                   private val twitterResponseMapper: TwitterResponseMapper,
                   private val sdotResponseMapper: SDOTResponseMapper
) {

    private val availableBridgesCache = mutableMapOf<String, AvailableBridge>()
    private val bridgeStatuses = mutableMapOf<String, BridgeStatus>()

    @Scheduled(fixedDelayString = "\${cache.bridges.refresh.intervalMillis}")
    fun getAvailableBridges(): Map<String, AvailableBridge> =
        availableBridgesCache.ifEmpty { refreshAvailableBridgeCache() }

    fun getBridgeStatuses(): Map<String, BridgeStatus> = bridgeStatuses.ifEmpty { refreshTweets() }

    fun getBridgeData(): Map<String, BridgeStatus>? = runBlocking {
        sdotResponseMapper.map(sdotProxy.pullBridgeData())
    }

    @Scheduled(fixedDelayString = "\${cache.tweets.refresh.intervalMillis}")
    private fun refreshTweets(): Map<String, BridgeStatus> = runBlocking {
        twitterResponseMapper.map(
            twitterResponse = twitterProxy.searchLatestTweets(),
            availableBridges = getAvailableBridges().values
        )
    }

    private fun refreshAvailableBridgeCache(): Map<String, AvailableBridge> =
        availableBridgesCache.plus(pullAndMapAvailableBridges())

    private fun pullAndMapAvailableBridges() = s3Proxy
        .getAvailableBridges()
        .associateBy({ it.cleanName() }, { AvailableBridge(id = it.id, name = it.name, lat = it.lat, lng = it.lng) })
        .toMap()
}