package com.justinsimonelli.brdges.data.service.proxy.gov

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.justinsimonelli.brdges.data.service.models.gov.BridgeData
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.springframework.beans.factory.annotation.Value
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Named

@Named
class SDOTProxy
@Inject
constructor(
    private val httpClient: HttpClient,
    jacksonObjectMapper: ObjectMapper,
    dateFormatter: DateTimeFormatter,
    @Value("\${sdot.url}") private val sdotUrl: String
): BaseProxy(jacksonObjectMapper, dateFormatter) {

    private val bridgeCache = mutableMapOf<String, CacheEntry>()

    suspend fun pullBridgeData(): List<BridgeData> {
        val bridgeData = deserializeDataToList(httpClient.get<String>(sdotUrl))

        bridgeData.forEach {
            updateBridgeCacheData(bridgeCache, it)

            it.closedToTrafficAt = bridgeCache[it.cleanName()]?.closedToTrafficAt
            it.lastClosedToTrafficAt = bridgeCache[it.cleanName()]?.lastClosedToTrafficAt
        }

        return bridgeData
    }
}