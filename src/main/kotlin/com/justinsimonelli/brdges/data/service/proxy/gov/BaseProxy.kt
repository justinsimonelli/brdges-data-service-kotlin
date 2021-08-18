package com.justinsimonelli.brdges.data.service.proxy.gov

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.justinsimonelli.brdges.data.service.Constants
import com.justinsimonelli.brdges.data.service.models.gov.BridgeData
import org.apache.commons.text.StringEscapeUtils
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

open class BaseProxy(private val jacksonObjectMapper: ObjectMapper,
                     private val dateFormatter: DateTimeFormatter) {

    protected fun deserializeDataToList(rawString: String?): List<BridgeData> =
        rawString?.let { jacksonObjectMapper.readValue(stripEnclosingStrings(it)) } ?: emptyList()

    protected fun updateBridgeCacheData(
        cache: MutableMap<String, CacheEntry>,
        bridgeData: BridgeData
    ) {
        val nowDateTime = dateFormatter.format(ZonedDateTime.now())
        val openToTraffic = bridgeData.isOpenToTraffic()
        val inCache = cache.containsKey(bridgeData.cleanName())
        val closedToTrafficAndNotCached = !openToTraffic && !inCache
        val closedToTrafficInCacheAndEmptyClosedDate = !openToTraffic
                && inCache
                && cache[bridgeData.cleanName()]?.closedToTrafficAt.isNullOrEmpty()
        if (closedToTrafficAndNotCached || closedToTrafficInCacheAndEmptyClosedDate) {
            cache[bridgeData.cleanName()] = CacheEntry(
                closedToTrafficAt = nowDateTime,
                lastClosedToTrafficAt = nowDateTime
            )
        } else if (openToTraffic && inCache) {
            cache[bridgeData.cleanName()]?.closedToTrafficAt = null
        }
    }

    private fun stripEnclosingStrings(rawString: String) =
        StringEscapeUtils
            .unescapeJson(rawString)
            .removePrefix(Constants.ESCAPED_QUOTE)
            .removeSuffix(Constants.ESCAPED_QUOTE)

    protected data class CacheEntry(
        var closedToTrafficAt: String? = null,
        var lastClosedToTrafficAt: String? = null
    )
}