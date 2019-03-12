package net.tacomoon

import net.tacomoon.exception.HttpRequestException
import org.apache.http.client.methods.HttpGet
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
        check(header.isNotBlank()) { "param should not be empty" }
        headers[header] = value
    }

    fun param(param: String, value: String) {
        check(param.isNotBlank()) { "param should not be empty" }
        params[param] = value
    }

    fun get(url: String) {
        check(url.isNotBlank()) { "url should not be blank" }
        request = HttpGet(url)
    }

    internal fun execute(): String {
        build()

        client.execute(request).use { response ->
            val url: String = request.uri.toASCIIString()
            val code: Int = response.statusLine.statusCode

            if (response.statusLine.statusCode / 100 != 2) {
                throw HttpRequestException("Request to $url failed with code: $code,\nreason: ${response.statusLine.reasonPhrase}")
            }

            return EntityUtils.toString(response.entity)
        }
    }

    private fun build() {
        checkNotNull(request) { "http request not specified" }

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

fun buildHttpRequest(init: HttpRequestBuilder.() -> Unit): String {
    return HttpRequestBuilder().apply(init).execute()
}

private fun buildURI(uri: URI, init: URIBuilder.() -> Unit): URI {
    return URIBuilder(uri).apply(init).build()
}