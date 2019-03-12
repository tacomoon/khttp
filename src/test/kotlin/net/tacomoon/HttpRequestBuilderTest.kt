package net.tacomoon

import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class HttpRequestBuilderTest {

    private val client: CloseableHttpClient = spyk(HttpClients.createDefault())
    private val slot: CapturingSlot<HttpRequestBase> = slot()
    private val okResponse: CloseableHttpResponse = mockk()

    @BeforeEach
    internal fun beforeEach() {
        every { okResponse.statusLine.statusCode } returns 200
        every { okResponse.statusLine.reasonPhrase } returns ""
        every { okResponse.entity } returns StringEntity("")
        every { okResponse.close() } answers {}

        every { client.execute(capture(slot)) } returns okResponse
    }

    @ParameterizedTest
    @MethodSource("plain url provider")
    fun `plain url request`(data: RequestData) {
        buildHttpRequest {
            client(client)
            get(data.url)
        }

        verify { client.execute(any()) }
        verify { okResponse.close() }

        assertThat(slot.captured.uri.toASCIIString())
                .isEqualTo(data.url)
    }

    @ParameterizedTest
    @MethodSource("url and headers provider")
    fun `headers in request`(data: RequestData) {
        buildHttpRequest {
            client(client)
            get(data.url)
            for ((header, value) in data.headers) {
                header(header, value)
            }
        }

        verify { client.execute(any()) }
        verify { okResponse.close() }

        val actualHeadersMap: Map<String, String> = slot.captured.allHeaders
                .associateBy({ it.name }, { it.value })

        assertThat(actualHeadersMap)
                .containsAllEntriesOf(data.headers)
    }

    @ParameterizedTest
    @MethodSource("url and params provider")
    fun `params in request`(data: RequestData) {
        buildHttpRequest {
            client(client)
            get(data.url)
            for ((param, value) in data.params) {
                param(param, value)
            }
        }

        verify { client.execute(any()) }
        verify { okResponse.close() }

        SoftAssertions.assertSoftly { softly ->
            for ((param, value) in data.params) {
                softly.assertThat(slot.captured.uri.toASCIIString())
                        .contains("$param=$value")
            }
        }
    }

    data class RequestData(val url: String, val headers: Map<String, String>, val params: Map<String, String>)

    @Suppress("unused")
    private companion object {
        @JvmStatic
        fun `plain url provider`(): Stream<RequestData> = Stream.of(
                RequestData("https://google.com", mapOf(), mapOf()),
                RequestData("https://en.wikipedia.org/wiki", mapOf(), mapOf())
        )

        @JvmStatic
        fun `url and headers provider`(): Stream<RequestData> = Stream.of(
                RequestData("https://google.com", mapOf("content-type" to "text/html"), mapOf())
        )

        @JvmStatic
        fun `url and params provider`(): Stream<RequestData> = Stream.of(
                RequestData("https://google.com/search", mapOf(), mapOf("q" to "test")),
                RequestData("https://google.com/search", mapOf(), mapOf("newwindow" to "1", "q" to "test"))
        )
    }
}