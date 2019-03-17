package net.tacomoon

import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.net.URI

class HttpRequestBuilder {
    private companion object {
        val defaultClient: CloseableHttpClient = HttpClients.createDefault()
    }

    private var client: CloseableHttpClient = defaultClient
    private lateinit var request: HttpRequestBase

    private val headers: MutableMap<String, String> = mutableMapOf()
    private val params: MutableMap<String, String> = mutableMapOf()


    fun client(client: CloseableHttpClient) {
        this.client = client
    }

    fun header(header: String, value: String) {
        check(header.isNotBlank()) { "'header' should not be empty" }
        headers[header] = value
    }

    fun param(param: String, value: String) {
        check(param.isNotBlank()) { "'param' should not be empty" }
        params[param] = value
    }

    fun get(url: String) {
        check(url.isNotBlank()) { "'url' should not be blank" }
        request = HttpGet(url)
    }

    fun post(url: String) {
        check(url.isNotBlank()) { "'url' should not be blank" }
        request = HttpPost(url)
    }

    fun put(url: String) {
        check(url.isNotBlank()) { "'url' should not be blank" }
        request = HttpPut(url)
    }

    fun delete(url: String) {
        check(url.isNotBlank()) { "'url' should not be blank" }
        request = HttpDelete(url)
    }

    internal fun executeRequest(): HttpResponse {
        buildRequest()

        client.execute(request).use { response ->
            val url: String = request.uri.toASCIIString()
            val code: Int = response.statusLine.statusCode
            val body: String = if (response.entity == null) "" else EntityUtils.toString(response.entity)

            return HttpResponse(url, code, body)
        }
    }

    private fun buildRequest() {
        checkNotNull(request) { "http method not specified" }

        headers.forEach { (header, value) ->
            request.addHeader(header, value)
        }

        if (params.isNotEmpty()) {
            val uri: URI = buildURI(request.uri) {
                params.forEach { param, value -> addParameter(param, value) }
            }
            request.uri = uri
        }
    }
}

fun request(init: HttpRequestBuilder.() -> Unit): HttpResponse {
    return HttpRequestBuilder().apply(init).executeRequest()
}

private fun buildURI(uri: URI, init: URIBuilder.() -> Unit): URI {
    return URIBuilder(uri).apply(init).build()
}