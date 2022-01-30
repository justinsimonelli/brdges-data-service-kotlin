package com.justinsimonelli.brdges.data.service.proxy.gov

import com.justinsimonelli.brdges.data.service.cache.CacheFactory
import com.justinsimonelli.brdges.data.service.cache.CacheKey
import com.justinsimonelli.brdges.data.service.extensions.deserializeDataToList
import com.justinsimonelli.brdges.data.service.extensions.update
import com.justinsimonelli.brdges.data.service.models.gov.BridgeData
import com.justinsimonelli.brdges.data.service.proxy.DataFetcher
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.springframework.beans.factory.annotation.Value
import javax.inject.Inject
import javax.inject.Named

@Named
class SDOTProxy
@Inject
constructor(
    private val httpClient: HttpClient,
    cacheFactory: CacheFactory,
    @Value("\${sdot.url}") private val sdotUrl: String
): DataFetcher<List<BridgeData>> {

    private val statusCache = cacheFactory.forKey<String, CacheEntry>(CacheKey.SDOT)

    override suspend fun fetch(spoofName: String?): List<BridgeData> {
        val availableBridgeData = httpClient.get<String>(sdotUrl).deserializeDataToList()

        availableBridgeData.forEach { availableBridge ->
            statusCache.update(availableBridge).also { latestStatus ->
                availableBridge.closedToTrafficAt = latestStatus.closedToTrafficAt
                availableBridge.reopenedToTrafficAt = latestStatus.reopenedToTrafficAt
            }
        }

        return availableBridgeData
    }
}