package com.justinsimonelli.brdges.data.service.proxy.gov

import com.justinsimonelli.brdges.data.service.cache.CacheFactory
import com.justinsimonelli.brdges.data.service.cache.CacheKey
import com.justinsimonelli.brdges.data.service.extensions.deserializeDataToList
import com.justinsimonelli.brdges.data.service.extensions.update
import com.justinsimonelli.brdges.data.service.models.gov.BridgeData
import com.justinsimonelli.brdges.data.service.proxy.DataFetcher
import javax.inject.Inject
import javax.inject.Named

@Named
class SpoofSDOTProxy
@Inject
constructor(
    cacheFactory: CacheFactory
) : DataFetcher<List<BridgeData>> {

    private val statusCache = cacheFactory.forKey<String, CacheEntry>(CacheKey.SPOOF)

    override suspend fun fetch(spoofName: String?): List<BridgeData> {
        val availableBridgeData = availableBridgesSpoofResponse(spoofName)

        availableBridgeData?.forEach { availableBridge ->
            statusCache.update(availableBridge).also { latestStatus ->
                availableBridge.closedToTrafficAt = latestStatus.closedToTrafficAt
                availableBridge.reopenedToTrafficAt = latestStatus.reopenedToTrafficAt
            }
        }

        return availableBridgeData ?: emptyList()
    }

    private fun availableBridgesSpoofResponse(spoofName: String?): List<BridgeData>? = spoofName.let {
        SDOTProxy::class.java.getResource("/spoof/${it}.txt")?.let { spoofFile ->
            spoofFile.readText().deserializeDataToList()
        }
    }
}