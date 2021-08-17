package com.justinsimonelli.brdges.data.service.proxy.gov

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.justinsimonelli.brdges.data.service.models.gov.BridgeData
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.apache.commons.text.StringEscapeUtils
import org.springframework.beans.factory.annotation.Value
import javax.inject.Inject
import javax.inject.Named

private const val ESCAPED_QUOTE = "\""

@Named
class SDOTProxy
@Inject
constructor(private val httpClient: HttpClient,
            private val jacksonObjectMapper: ObjectMapper,
            @Value("\${sdot.url}") private val sdotUrl: String) {

    suspend fun pullBridgeData(): List<BridgeData>? {
        return httpClient
            .get<String?>(sdotUrl)
            ?.let { jacksonObjectMapper.readValue(stripEnclosingStrings(it)) }
    }

    private fun stripEnclosingStrings(rawString: String) =
        StringEscapeUtils.unescapeJson(rawString).removePrefix(ESCAPED_QUOTE).removeSuffix(ESCAPED_QUOTE)
}