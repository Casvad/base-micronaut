package com.carlos.repositories

import com.carlos.BaseTest
import com.carlos.models.ExampleEntity
import com.carlos.services.cache.CacheServiceInterface
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
@MicronautTest(transactional = false)
class CacheServiceInterfaceTest : BaseTest() {

    @Inject
    private lateinit var cacheServiceInterface: CacheServiceInterface

    @BeforeEach
    fun flush() {
        this.flushDatabase()
        this.startMockServer()
    }

    @AfterEach
    fun stop() {
        this.stopMockServer()
    }

    @Test
    fun `GIVEN CacheServiceInterfaceTest WHEN save SHOULD save ok`() {

        this.cacheServiceInterface.setKey(
            key = "hello",
            ttl = 60,
            data = ExampleEntity(
                name = "Carlos",
                additionalInformation = mapOf("hello" to "World")
            )
        )

        this.awaitUntilAssert {

            val data = this.cacheServiceInterface.getKey("hello", ExampleEntity::class.java)

            Assertions.assertNotNull(data, "Db model not in redis database")
            Assertions.assertEquals("World", data!!.additionalInformation["hello"])
        }
    }
}