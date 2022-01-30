package com.justinsimonelli.brdges.data.service.extensions

import com.justinsimonelli.brdges.data.service.Constants
import com.justinsimonelli.brdges.data.service.models.gov.BridgeData
import com.justinsimonelli.brdges.data.service.proxy.gov.CacheEntry
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern(Constants.SDOT_DATE_FORMAT)

fun MutableMap<String, CacheEntry>.update(bridgeData: BridgeData): CacheEntry {
    val bridgeCleanName = bridgeData.cleanName()
    val nowDateTime = dateFormatter.format(ZonedDateTime.now(ZoneId.of(Constants.ZONE_PST, ZoneId.SHORT_IDS)))
    val closedToTraffic = !bridgeData.isOpenToTraffic()
    val cachedData = this[bridgeCleanName]

    val cacheEntry = if (closedToTraffic) {
        CacheEntry(closedToTrafficAt = nowDateTime)
    } else if (!cachedData?.reopenedToTrafficAt.isNullOrEmpty()) {
        CacheEntry(reopenedToTrafficAt = cachedData?.reopenedToTrafficAt)
    } else if (!cachedData?.closedToTrafficAt.isNullOrEmpty()){
        CacheEntry(reopenedToTrafficAt = nowDateTime)
    } else {
        CacheEntry(closedToTrafficAt = null, reopenedToTrafficAt = null)
    }

    this[bridgeCleanName] = cacheEntry

    return cacheEntry
}