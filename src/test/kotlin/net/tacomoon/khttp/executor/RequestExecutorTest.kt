package net.tacomoon.khttp.executor

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.tacomoon.khttp.model.HttpResponse
import net.tacomoon.khttp.utils.mockResponse
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.util.EntityUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class RequestExecutorTest {

    private val client: CloseableHttpClient = mockk()

    @ParameterizedTest
    @MethodSource("request provider")
    fun `request execution`(request: HttpRequestBase) {
        every { client.execute(any()) } answers { mockResponse() }

        request.execute(client)

        verify { client.execute(request) }
    }

    @ParameterizedTest
    @MethodSource("request response provider")
    fun `response building`(request: HttpRequestBase, response: CloseableHttpResponse) {
        every { client.execute(any()) } returns response

        val parsed: HttpResponse = request.execute(client)

        assertThat(parsed.url).isEqualTo(request.uri.toASCIIString())
        assertThat(parsed.code).isEqualTo(response.statusLine.statusCode)
        assertThat(parsed.body).isEqualTo(EntityUtils.toString(response.entity))
    }

    @Suppress("unused")
    private companion object {
        @JvmStatic
        fun `request provider`(): Stream<HttpRequestBase> {
            return Stream.of(
                    HttpGet("https://site.com"),
                    HttpPut("https://site.com"),
                    HttpPost("https://site.com"),
                    HttpDelete("https://site.com")
            )
        }

        @JvmStatic
        fun `request response provider`(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(HttpGet("https://site.com"), mockResponse()),
                    Arguments.of(HttpPut("https://site.com"), mockResponse()),
                    Arguments.of(HttpPost("https://site.com"), mockResponse()),
                    Arguments.of(HttpDelete("https://site.com"), mockResponse())
            )
        }
    }
}