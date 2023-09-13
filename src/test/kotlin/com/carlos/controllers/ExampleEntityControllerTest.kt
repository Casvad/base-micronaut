package com.carlos.controllers

import com.carlos.BaseTest
import com.carlos.models.ExampleEntity
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.annotation.Client
import io.micronaut.rxjava2.http.client.RxHttpClient
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
@MicronautTest
class ExampleEntityControllerTest: BaseTest() {

    @Inject
    @field:Client("/")
    lateinit var client: RxHttpClient

//    @Inject
//    private lateinit var toMock: MockClass
//
//    @MockBean(MockClass::class)
//    fun getMockClass(): MockClass {
//        return Mockito.mock(MockClass::class.java)
//    }

    @Test
    fun `GIVEN ExampleController WHEN create SHOULD create ok`() {

        val response: ExampleEntity = this.client.toBlocking().retrieve(
            HttpRequest.POST<Any>("api/ms/base-micronaut/example-entities", """{ "name": "Hello"  }""").header(
                "X-API-VERSION", "2"
            )
        ).toObject()

        Assertions.assertEquals("Hello", response.name)
    }
}