package com.carlos.controllers

import com.carlos.models.ExampleEntity
import io.micronaut.context.annotation.Parameter
import io.micronaut.core.version.annotation.Version
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.micronaut.validation.Validated
import io.reactivex.Observable
import io.reactivex.Single
import javax.validation.Valid

@Controller("example-entities")
@Validated
open class ExampleEntityController {
    @Version("1")
    @Post
    open fun create(
        @Header("token") token: String?,
        @Body @Valid request: ExampleEntity
    ): MutableHttpResponse<Any> {

        return HttpResponse.status(HttpStatus.GONE)
    }

    @Version("2")
    @Post
    @ExecuteOn(TaskExecutors.IO)
    open fun createV2(
        @Header("token") token: String?,
        @Body @Valid request: ExampleEntity,
        httpRequest: HttpRequest<*>
    ): Single<ExampleEntity> {

        return Single.fromCallable {
            request
        }
    }

    @Version("2")
    @Get
    @ExecuteOn(TaskExecutors.IO)
    open fun findAll(
        @Body @Valid request: ExampleEntity,
        @Parameter("per-page") perPage: Int? = null,
        httpRequest: HttpRequest<*>
    ): Observable<ExampleEntity> {

        return Observable.fromIterable(listOf(ExampleEntity()))
    }
}