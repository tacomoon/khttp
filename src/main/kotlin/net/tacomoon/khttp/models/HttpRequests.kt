package net.tacomoon.khttp.models

import net.tacomoon.khttp.utils.EntityMapper
import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.HttpEntityEnclosingRequest
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicHeader

sealed class BaseHttpRequest(protected val request: HttpRequestBase) {
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

class HttpRequest(request: HttpRequestBase) : BaseHttpRequest(request)

class HttpRequestEnclosingEntity(request: HttpEntityEnclosingRequestBase) : BaseHttpRequest(request) {

    fun entity(entity: HttpEntity) {
        (request as HttpEntityEnclosingRequest).entity = entity
    }

    fun entity(entity: String) {
        (request as HttpEntityEnclosingRequest).entity = StringEntity(entity)
    }

    fun <T> entity(entity: T) {
        entity(EntityMapper.mapper.writeValueAsString(entity))
    }
}