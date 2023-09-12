package com.carlos.services.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.lettuce.core.SetArgs
import io.lettuce.core.api.StatefulRedisConnection
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Requires(property = "redis")
@Singleton
open class RedisService(
    private val connection: StatefulRedisConnection<String, String>,
    private val objectMapper: ObjectMapper,
    @Property(name = "redis.prefix") private val redisPrefix: String
) : CacheServiceInterface {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun <T : Any?> getKey(key: String, classRef: Class<T>): T? {

        return try {
            this.connection.sync().get(redisPrefix + key)?.let {
                if (classRef == String::class.java) {
                    it as T
                } else this.objectMapper.readValue(it, classRef)
            }
        } catch (ex: Exception) {
            this.logger.error("error getting key from redis {} with error {}", key, ex.message, ex)
            null
        }
    }

    override fun <T : Any?> getKey(key: String, typeReference: TypeReference<T>): T? {

        return try {
            this.connection.sync().get(redisPrefix + key)?.let {
                this.objectMapper.readValue(it, typeReference)
            }
        } catch (ex: Exception) {
            this.logger.error("error getting keyList from redis {} with error {}", key, ex.message, ex)
            null
        }
    }

    override fun existKey(key: String): Boolean {
        return this.connection.sync().exists(redisPrefix + key) == 1L
    }

    @Async
    override fun deleteKeys(vararg keys: String) {

        if (keys.isNotEmpty()) {
            this.connection.sync().del(*keys.map { redisPrefix + it }.toTypedArray())
        }
    }

    override fun deleteKeysSync(vararg keys: String) {

        if (keys.isNotEmpty()) {
            this.connection.sync().del(*keys.map { redisPrefix + it }.toTypedArray())
        }
    }

    override fun getLock(key: String, ttlInSeconds: Long): Boolean {

        val args = SetArgs().nx().ex(ttlInSeconds)

        val couldSet = this.connection.sync().set(
            this.redisPrefix + key, "lock", args
        )

        return couldSet == "OK"
    }

    override fun incrementAndGet(key: String, ttlInSeconds: Long): Long {
        return try {
            val incResult = this.connection.sync().incr(redisPrefix + key)

            if (incResult == 1L) {
                // Use nx command https://github.com/lettuce-io/lettuce-core/issues/1905 when redis version > 6.2.0.RELEASE
                this.connection.sync().expire(redisPrefix + key, ttlInSeconds)
            }

            incResult
        } catch (ex: Exception) {
            this.logger.error("error inc and get {} because {}", key, ex.message, ex)

            0
        }
    }

    override fun <T> setKeyIfAbsent(key: String, ttl: Long, data: T): Boolean {
        return try {
            val dataString = if (data is String) data else this.objectMapper.writeValueAsString(data)

            val args = SetArgs().nx().ex(ttl)
            val couldSet = this.connection.sync().set(
                this.redisPrefix + key, dataString, args
            )
            couldSet == "OK"
        } catch (ex: Exception) {
            this.logger.error("error setting redis key {} because {}", key, ex.message, ex)
            return false
        }
    }

    override fun <T : Any?> setKey(key: String, ttl: Long, data: T) {

        try {
            val dataString = if (data is String) data else this.objectMapper.writeValueAsString(data)
            this.connection.sync().setex(this.redisPrefix + key, ttl, dataString)
        } catch (ex: Exception) {
            this.logger.error("error setting redis key {} because {}", key, ex.message, ex)
        }
    }

    override fun <T : Any?> remember(key: String, ttl: Long, classRef: Class<T>, callback: () -> T?): T? {

        return this.getKey(key, classRef) ?: run {
            callback()?.let {
                this.setKey(key, ttl, it)
                it
            }
        }
    }

    override fun <T : Any?> remember(key: String, ttl: Long, typeReference: TypeReference<T>, callback: () -> T?): T? {

        return this.getKey(key, typeReference) ?: run {
            callback()?.let {
                this.setKey(key, ttl, it)
                it
            }
        }
    }
}