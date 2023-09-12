package com.carlos.repositories

import com.carlos.BaseTest
import com.carlos.models.ExampleEntity
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
class ExampleEntityRepositoryTest : BaseTest() {


    @Inject
    private lateinit var exampleEntityRepository: ExampleEntityRepository

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
    fun `GIVEN ExampleEntityRepository WHEN save SHOULD save ok`() {

        val entity = this.exampleEntityRepository.save(
            ExampleEntity(
                name = "Carlos",
                additionalInformation = mapOf("hello" to "World")
            )
        )

        val dbModel = this.exampleEntityRepository.findById(entity.id)

        Assertions.assertTrue(dbModel.isPresent, "Db model not in database")
        Assertions.assertEquals("World", dbModel.get().additionalInformation["hello"])
    }
}