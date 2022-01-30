package com.justinsimonelli.brdges.data.service.injection

import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder
import com.fasterxml.jackson.databind.DeserializationFeature
import com.justinsimonelli.brdges.data.service.Constants
import com.justinsimonelli.brdges.data.service.cache.CacheFactory
import com.justinsimonelli.brdges.data.service.cache.CacheKey
import com.justinsimonelli.brdges.data.service.proxy.gov.CacheEntry
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

@Component
class ObjectProvider {

    @Bean
    fun awsSecretsManager() =
        AWSSecretsManagerClientBuilder.standard().withRegion(Regions.US_WEST_2).build()

    @Bean
    fun amazonS3() = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build()

    @Bean
    fun httpClientProvider(): HttpClient = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }

    @Bean
    fun sdotDateFormatter() = DateTimeFormatter.ofPattern(Constants.SDOT_DATE_FORMAT)


    @Bean
    fun jacksonObjectMapper() = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    @Bean
    fun cacheFactory(): CacheFactory {
        return CacheFactory
            .Registry
            .register(CacheKey.SDOT, mutableMapOf<String, CacheEntry>())
            .register(CacheKey.SPOOF, mutableMapOf<String, CacheEntry>())
            .build()
    }
}