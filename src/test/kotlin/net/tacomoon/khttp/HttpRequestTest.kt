package net.tacomoon.khttp

import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import net.tacomoon.khttp.builder.get
import net.tacomoon.khttp.utils.mockResponse
import org.apache.http.Header
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.message.BasicHeader
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class HttpRequestTest {
    private val client: CloseableHttpClient = mockk()
    private val requestSlot: CapturingSlot<HttpRequestBase> = slot()

    @BeforeEach
    fun beforeEach() {
        every { client.execute(capture(requestSlot)) } answers { mockResponse(200, "") }
    }

    @ParameterizedTest
    @MethodSource("header provider")
    fun `build request with header`(expected: Header) {
        get("https://example.com", client) {
            header(expected)
        }

        assertThat(requestSlot.captured.allHeaders)
                .`as`("Expecting HTTP request to contains header")
                .containsOnly(expected)
    }

    @ParameterizedTest
    @MethodSource("header name value provider")
    fun `build request with name value header`(expectedName: String, expectedValue: String) {
        get("https://example.com", client) {
            header(expectedName, expectedValue)
        }

        assertThat(requestSlot.captured.allHeaders)
                .`as`("Expecting HTTP request to contains header")
                .hasSize(1)
                .matches { it[0].name == expectedName }
                .matches { it[0].value == expectedValue }
    }

    @Suppress("unused")
    private companion object {
        @JvmStatic
        fun `header provider`(): Stream<Header> {
            return Stream.of(
                    BasicHeader("content-type", "application/json"),
                    BasicHeader("Authorization", "bearer IsInRMSJ9.eyJkI")
            )
        }

        @JvmStatic
        fun `header name value provider`(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of("content-type", "application/json"),
                    Arguments.of("Authorization", "bearer IsInRMSJ9.eyJkI")
            )
        }
    }
}