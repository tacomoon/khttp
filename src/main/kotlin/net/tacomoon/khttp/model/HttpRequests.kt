package net.tacomoon.khttp.model

import net.tacomoon.khttp.mapper.EntityMapper
import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicHeader

sealed class BaseHttpRequest<T : HttpRequestBase>(protected val request: T) {
    private val headers: MutableList<Header> = mutableListOf()

    fun header(header: Header) {
        headers.add(header)
    }

    fun header(name: String, value: String) {
        check(name.isNotBlank()) { "'name' should not be blank" }
        headers.add(BasicHeader(name, value))
    }

    fun build(): HttpRequestBase {
        headers.forEach(request::addHeader)
        return request
    }
}

class HttpRequest(request: HttpRequestBase)
    : BaseHttpRequest<HttpRequestBase>(request)

class HttpRequestEnclosingEntity(request: HttpEntityEnclosingRequestBase)
    : BaseHttpRequest<HttpEntityEnclosingRequestBase>(request) {

    fun entity(entity: HttpEntity) {
        request.entity = entity
    }

    fun entity(entity: String) {
        entity(StringEntity(entity))
    }

    fun <T> entity(entity: T) {
        entity(EntityMapper.serialize(entity))
    }
}