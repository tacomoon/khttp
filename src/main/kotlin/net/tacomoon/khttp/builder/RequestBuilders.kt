package net.tacomoon.khttp.builder

import net.tacomoon.khttp.executor.RequestExecutor
import net.tacomoon.khttp.executor.execute
import net.tacomoon.khttp.model.HttpRequest
import net.tacomoon.khttp.model.HttpRequestEnclosingEntity
import net.tacomoon.khttp.model.HttpResponse
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.impl.client.CloseableHttpClient

fun get(url: String, client: CloseableHttpClient = RequestExecutor.defaultClient, init: (HttpRequest.() -> Unit)? = null): HttpResponse {
    val request = HttpRequest(HttpGet(url))

    init?.let { request.apply(it) }
    return request.build()
            .execute(client)
}

fun post(url: String, client: CloseableHttpClient = RequestExecutor.defaultClient, init: (HttpRequestEnclosingEntity.() -> Unit)? = null): HttpResponse {
    val request = HttpRequestEnclosingEntity(HttpPost(url))

    init?.let { request.apply(it) }
    return request.build()
            .execute(client)
}

fun put(url: String, client: CloseableHttpClient = RequestExecutor.defaultClient, init: (HttpRequestEnclosingEntity.() -> Unit)? = null): HttpResponse {
    val request = HttpRequestEnclosingEntity(HttpPut(url))

    init?.let { request.apply(it) }
    return request.build()
            .execute(client)
}

fun delete(url: String, client: CloseableHttpClient = RequestExecutor.defaultClient, init: (HttpRequest.() -> Unit)? = null): HttpResponse {
    val request = HttpRequest(HttpDelete(url))

    init?.let { request.apply(it) }
    return request.build()
            .execute(client)
}