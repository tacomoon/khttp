package net.tacomoon.khttp.utils

import net.tacomoon.khttp.models.HttpRequest
import net.tacomoon.khttp.models.HttpRequestEnclosingEntity
import net.tacomoon.khttp.models.HttpResponse
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut

fun get(url: String, init: (HttpRequest.() -> Unit)? = null): HttpResponse {
    val request = HttpRequest(HttpGet(url))

    init?.let { request.apply(it) }
    return request.build()
            .execute()
}

fun post(url: String, init: (HttpRequestEnclosingEntity.() -> Unit)? = null): HttpResponse {
    val request = HttpRequestEnclosingEntity(HttpPost(url))

    init?.let { request.apply(it) }
    return request.build()
            .execute()
}

fun put(url: String, init: (HttpRequestEnclosingEntity.() -> Unit)? = null): HttpResponse {
    val request = HttpRequestEnclosingEntity(HttpPut(url))

    init?.let { request.apply(it) }
    return request.build()
            .execute()
}

fun delete(url: String, init: (HttpRequest.() -> Unit)? = null): HttpResponse {
    val request = HttpRequest(HttpDelete(url))

    init?.let { request.apply(it) }
    return request.build()
            .execute()
}