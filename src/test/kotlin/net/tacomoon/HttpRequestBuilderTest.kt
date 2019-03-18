package net.tacomoon

import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class HttpRequestBuilderTest {

    private val client: CloseableHttpClient = spyk(HttpClients.createDefault())
    private val slot: CapturingSlot<HttpRequestBase> = slot()
    private lateinit var response: CloseableHttpResponse

    @BeforeEach
    internal fun beforeEach() {
        response = mockResponse()

        every { client.execute(capture(slot)) } returns response
    }

    @Test
    fun `build get request`() {
        request {
            client(client)
            get("https://example.com")
        }

        assertThat(slot.captured)
                .`as`("Expecting http method to be GET")
                .isInstanceOf(HttpGet::class.java)
    }

    @Test
    fun `build post request`() {
        request {
            client(client)
            post("https://example.com")
        }

        assertThat(slot.captured)
                .`as`("Expecting http method to be POST")
                .isInstanceOf(HttpPost::class.java)
    }

    @Test
    fun `build put request`() {
        request {
            client(client)
            put("https://example.com")
        }

        assertThat(slot.captured)
                .`as`("Expecting http method to be PUT")
                .isInstanceOf(HttpPut::class.java)
    }

    @Test
    fun `build delete request`() {
        request {
            client(client)
            delete("https://example.com")
        }

        assertThat(slot.captured)
                .`as`("Expecting http method to be DELETE")
                .isInstanceOf(HttpDelete::class.java)
    }

    @ParameterizedTest
    @MethodSource("response provider")
    fun `response model`(expected: ResponseData) {
        every { client.execute(capture(slot)) } answers { mockResponse(expected.code, expected.body) }

        val actual: HttpResponse = request {
            client(client)
            get("https://example.com")
        }

        SoftAssertions.assertSoftly { softly ->
            softly.assertThat(actual.code).isEqualTo(expected.code)
            softly.assertThat(actual.body).isEqualTo(expected.body)
        }

    }

    @ParameterizedTest
    @MethodSource("plain url provider")
    fun `plain url request`(data: RequestData) {
        request {
            client(client)
            get(data.url)
        }

        verify { client.execute(any()) }
        verify { response.close() }

        assertThat(slot.captured)
                .isInstanceOf(HttpGet::class.java)

        assertThat(slot.captured.uri.toASCIIString())
                .isEqualTo(data.url)
    }

    @ParameterizedTest
    @MethodSource("url and headers provider")
    fun `headers in request`(data: RequestData) {
        request {
            client(client)
            get(data.url)
            for ((header, value) in data.headers) {
                header(header, value)
            }
        }

        verify { client.execute(any()) }
        verify { response.close() }

        val actualHeadersMap: Map<String, String> = slot.captured.allHeaders
                .associateBy({ it.name }, { it.value })

        assertThat(actualHeadersMap)
                .containsAllEntriesOf(data.headers)
    }

    @ParameterizedTest
    @MethodSource("url and params provider")
    fun `params in request`(data: RequestData) {
        request {
            client(client)
            get(data.url)
            for ((param, value) in data.params) {
                param(param, value)
            }
        }

        verify { client.execute(any()) }
        verify { response.close() }

        SoftAssertions.assertSoftly { softly ->
            for ((param, value) in data.params) {
                softly.assertThat(slot.captured.uri.toASCIIString())
                        .contains("$param=$value")
            }
        }
    }

    @ParameterizedTest
    @MethodSource("serializable models provider")
    fun `parse response`(data: SerializationData) {
        every { client.execute(capture(slot)) } answers { mockResponse(200, data.json) }

        val response: HttpResponse = request {
            client(client)
            get("https://example.com")
        }

        val actual: RequestData = response.parse()

        assertThat(actual).isEqualTo(data.model)
    }

    @Suppress("unused")
    companion object {
        data class ResponseData(val code: Int, val body: String)
        data class RequestData(val url: String, val headers: Map<String, String>, val params: Map<String, String>)
        data class SerializationData(val model: Any, val json: String)

        private fun mockResponse(code: Int = 200, body: String = ""): CloseableHttpResponse {
            val mock: CloseableHttpResponse = mockk()

            every { mock.statusLine.statusCode } returns code
            every { mock.entity } returns StringEntity(body)
            every { mock.close() } answers {}

            return mock
        }

        @JvmStatic
        fun `response provider`(): Stream<ResponseData> = Stream.of(
                ResponseData(504, ""),
                ResponseData(400, "{}"),
                ResponseData(200, "{status:\"OK\"}")
        )

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

        @JvmStatic
        fun `serializable models provider`(): Stream<SerializationData> = Stream.of(
                SerializationData(
                        RequestData("https://google.com", mapOf(), mapOf()),
                        """{"url":"https://google.com","headers":{},"params":{}}"""
                ),
                SerializationData(
                        RequestData("https://google.com", mapOf("content-type" to "text/html"), mapOf("q" to "test")),
                        """{"url":"https://google.com","headers":{"content-type":"text/html"},"params":{"q":"test"}}"""
                ),
                SerializationData(
                        RequestData("https://google.com", mapOf("content-type" to "text/html"), mapOf("newwindow" to "1", "q" to "test")),
                        """{"url":"https://google.com","headers":{"content-type":"text/html"},"params":{"newwindow":"1","q":"test"}}"""
                )
        )
    }
}