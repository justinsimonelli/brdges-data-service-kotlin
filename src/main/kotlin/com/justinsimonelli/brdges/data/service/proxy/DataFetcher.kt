package com.justinsimonelli.brdges.data.service.proxy

interface DataFetcher<T> {
    suspend fun fetch(spoofName: String? = null): T
}