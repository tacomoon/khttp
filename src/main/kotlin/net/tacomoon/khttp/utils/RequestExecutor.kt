package net.tacomoon.khttp.utils

import net.tacomoon.khttp.models.HttpResponse
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

object RequestExecutor {
    val defaultClient: CloseableHttpClient = HttpClients.createDefault()
}

fun HttpRequestBase.execute(): HttpResponse {
    RequestExecutor.defaultClient.execute(this).use { response: CloseableHttpResponse ->
        val url: String = uri.toASCIIString()
        val code: Int = response.statusLine.statusCode
        val body: String = if (response.entity == null) "" else EntityUtils.toString(response.entity)

        return HttpResponse(url, code, body)
    }
}