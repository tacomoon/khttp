package net.tacomoon.khttp

import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import net.tacomoon.khttp.utils.EntityMapper
import net.tacomoon.khttp.utils.post
import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.util.EntityUtils
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class HttpRequestWithBodyTest {
    private val client: CloseableHttpClient = mockk()
    private val requestSlot: CapturingSlot<HttpEntityEnclosingRequestBase> = slot()

    @BeforeEach
    fun beforeEach() {
        every { client.execute(capture(requestSlot)) } answers { mockResponse() }
    }

    @ParameterizedTest
    @MethodSource("body provider")
    fun `build request with body`(expected: HttpEntity) {
        post("https://example.com", client) {
            entity(expected)
        }

        Assertions.assertThat(requestSlot.captured.entity)
                .`as`("Expecting HTTP request to contains body")
                .isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("string body provider")
    fun `build request with string body`(expected: String) {
        post("https://example.com", client) {
            entity(expected)
        }

        Assertions.assertThat(requestSlot.captured.entity)
                .`as`("Expecting HTTP request to contains body")
                .extracting { EntityUtils.toString(it) }
                .isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("object body provider")
    fun `build request with object body`(expected: TestBody) {
        post("https://example.com", client) {
            entity(expected)
        }

        Assertions.assertThat(requestSlot.captured.entity)
                .`as`("Expecting HTTP request to contains body")
                .extracting { EntityUtils.toString(it) }
                .extracting { EntityMapper.mapper.readValue(it as String, TestBody::class.java) }
                .isEqualTo(expected)
    }

    data class TestBody(val field: String, val value: String)

    @Suppress("unused")
    private companion object {
        @JvmStatic
        fun `body provider`(): Stream<HttpEntity> {
            return Stream.of(
                    StringEntity("""{"user":"admin"}"""),
                    StringEntity("""{"field":"ultimate_question","value":"42"}""")
            )
        }

        @JvmStatic
        fun `string body provider`(): Stream<String> {
            return Stream.of(
                    """{"user":"admin"}""",
                    """{"field":"ultimate_question","value":"42"}"""
            )
        }

        @JvmStatic
        fun `object body provider`(): Stream<TestBody> {
            return Stream.of(TestBody("ultimate_question", "42"))
        }
    }
}