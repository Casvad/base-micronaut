package com.carlos.services.cache

import com.fasterxml.jackson.core.type.TypeReference

interface CacheServiceInterface {

    fun <T : Any?> remember(key: String, ttl: Long, classRef: Class<T>, callback: () -> T?): T?
    fun <T : Any?> remember(key: String, ttl: Long, typeReference: TypeReference<T>, callback: () -> T?): T?
    fun <T : Any?> setKey(key: String, ttl: Long, data: T)
    fun <T : Any?> getKey(key: String, classRef: Class<T>): T?
    fun <T : Any?> getKey(key: String, typeReference: TypeReference<T>): T?
    fun existKey(key: String): Boolean
    fun deleteKeys(vararg keys: String)
    fun deleteKeysSync(vararg keys: String)
    fun getLock(key: String, ttlInSeconds: Long): Boolean
    fun incrementAndGet(key: String, ttlInSeconds: Long): Long
    fun <T : Any?> setKeyIfAbsent(key: String, ttl: Long, data: T): Boolean
}