package com.carlos

import com.carlos.utils.FileManagerUtil
import com.fasterxml.jackson.databind.ObjectMapper
import io.lettuce.core.api.StatefulRedisConnection
import io.micronaut.transaction.SynchronousTransactionManager
import jakarta.inject.Inject
import org.awaitility.Awaitility
import org.junit.jupiter.api.Assertions
import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import java.sql.Connection

open class BaseTest {

    @Inject
    protected lateinit var objectMapper: ObjectMapper

    @Inject
    protected lateinit var connection: Connection

    @Inject
    protected lateinit var transactionManager: SynchronousTransactionManager<Connection>

    @Inject
    protected lateinit var redis: StatefulRedisConnection<String, String>

    private lateinit var mockServer: ClientAndServer

    fun startMockServer() {
        this.mockServer = startClientAndServer(8099)
    }

    fun stopMockServer() {
        this.mockServer.stop()
    }

    fun flushDatabase() {

        val keys = this.redis.sync().keys("*")
        if (keys.isNotEmpty()) {
            this.redis.sync().del(*keys.toTypedArray())
        }

        this.transactionManager.executeWrite {
            this.connection.prepareStatement(FileManagerUtil.getQuery("clean.sql"))
                .execute()
        }
    }

    fun runQuery(query: String) {
        this.transactionManager.executeWrite {
            this.connection.prepareStatement(query)
                .execute()
        }
    }

    fun runQueryFromFile(file: String) {

        this.runQuery(
            FileManagerUtil.getQuery(file)
        )
    }

    fun mockServerCall(method: String, path: String, responseFile: String, code: Int = 200) {

        this.mockServer.`when`(
            request()
                .withMethod(method)
                .withPath(path)
        ).respond(
            response()
                .withStatusCode(code)
                .withHeader("Content-Type", "application/json")
                .withBody(this.getJsonFile(responseFile))
        )
    }

    fun verifyServerCalls(method: String, path: String, calls: Int) {

        val realCalls = this.mockServer.retrieveRecordedRequests(
            request()
                .withMethod(method)
                .withPath(path)
        )

        Assertions.assertEquals(
            calls,
            realCalls.size,
            "Error verify server calls expected $calls but found ${realCalls.size} for $method-$path"
        )
    }

    fun getJsonFile(fileName: String): String {

        return FileManagerUtil.getFile("/json/", "$fileName.json")
    }

    protected fun awaitUntilAssert(assertion: () -> Unit) {
        Awaitility.await().untilAsserted {
            assertion()
        }
    }

    protected inline fun <reified T> getObjectFromFile(fileName: String, replaceFun: (String) -> String = { it }): T {

        return objectMapper.readValue(
            replaceFun(this.getJsonFile(fileName)),
            T::class.java
        )
    }

    protected inline fun <reified T> String.toObject(): T {

        return objectMapper.readValue(this, T::class.java)
    }
}