package com.justinsimonelli.brdges.data.service.cache

object CacheFactory {
    private var cacheRegistry = mutableMapOf<String, Map<*, *>>()

    fun <K, V> forKey(cacheKey: CacheKey): MutableMap<K, V> {
        return cacheRegistry[cacheKey.name] as  MutableMap<K, V>
    }

    object Registry {
        fun register(cacheKey: CacheKey, cacheMap: MutableMap<*, *>): Registry {
            val cacheKeyName = cacheKey.name
            if (cacheRegistry.containsKey(cacheKeyName)) {
                throw IllegalArgumentException("cache already exists for key = $cacheKey")
            }

            cacheRegistry[cacheKeyName] = cacheMap

            return this
        }

        fun build(): CacheFactory = CacheFactory
    }
}