package com.justinsimonelli.brdges.data.service.extensions

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import com.justinsimonelli.brdges.data.service.Constants
import com.justinsimonelli.brdges.data.service.models.gov.BridgeData
import org.apache.commons.text.StringEscapeUtils

private val jacksonObjectMapper =
    com.fasterxml.jackson.module.kotlin.jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

fun String?.deserializeDataToList(): List<BridgeData> {
    return this?.let { jacksonObjectMapper.readValue(stripEnclosingStrings(it)) } ?: emptyList()
}

private fun stripEnclosingStrings(rawString: String) =
    StringEscapeUtils
        .unescapeJson(rawString)
        .removePrefix(Constants.ESCAPED_QUOTE)
        .removeSuffix(Constants.ESCAPED_QUOTE)