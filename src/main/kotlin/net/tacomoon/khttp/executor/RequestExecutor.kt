package net.tacomoon.khttp.executor

import net.tacomoon.khttp.model.HttpResponse
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

object RequestExecutor {
    val defaultClient: CloseableHttpClient = HttpClients.createDefault()
}

fun HttpRequestBase.execute(client: CloseableHttpClient): HttpResponse {
    client.execute(this).use { response: CloseableHttpResponse ->
        val url: String = uri.toASCIIString()
        val code: Int = response.statusLine.statusCode
        val body: String = if (response.entity == null) "" else EntityUtils.toString(response.entity)

        return HttpResponse(url, code, body)
    }
}