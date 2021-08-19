package com.justinsimonelli.brdges.data.service.proxy.gov

import com.fasterxml.jackson.databind.ObjectMapper
import com.justinsimonelli.brdges.data.service.models.gov.BridgeData
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Named

@Named
class SpoofSDOTProxy
@Inject
constructor(
    jacksonObjectMapper: ObjectMapper,
    dateFormatter: DateTimeFormatter
) : BaseProxy(jacksonObjectMapper, dateFormatter) {

    private val bridgeCache = mutableMapOf<String, CacheEntry>()

    fun pullBridgeData(spoofName: String?): List<BridgeData> {
        val bridgeData = deserializeDataToList(spoofResponse(spoofName = spoofName))

        bridgeData.forEach {
            updateBridgeCacheData(bridgeCache, it)

            it.closedToTrafficAt = bridgeCache[it.cleanName()]?.closedToTrafficAt
            it.reopenedToTrafficAt = bridgeCache[it.cleanName()]?.reopenedToTrafficAt
        }

        return bridgeData
    }

    private fun spoofResponse(spoofName: String?): String = spoofName.let {
        SDOTProxy::class.java.getResource("/spoof/${it}.txt").readText()
    }
}