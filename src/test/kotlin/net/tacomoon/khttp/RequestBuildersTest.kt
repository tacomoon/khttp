package net.tacomoon.khttp

import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import net.tacomoon.khttp.builder.delete
import net.tacomoon.khttp.builder.get
import net.tacomoon.khttp.builder.post
import net.tacomoon.khttp.builder.put
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.impl.client.CloseableHttpClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class RequestBuildersTest {
    private val client: CloseableHttpClient = mockk()
    private val requestSlot: CapturingSlot<HttpRequestBase> = slot()

    @BeforeEach
    fun beforeEach() {
        every { client.execute(capture(requestSlot)) } answers { mockResponse() }
    }

    @Test
    fun `build get request`() {
        get("https://example.com", client)

        assertThat(requestSlot.captured)
                .`as`("Expecting HTTP method to be GET")
                .isInstanceOf(HttpGet::class.java)
    }

    @Test
    fun `build post request`() {
        post("https://example.com", client)

        assertThat(requestSlot.captured)
                .`as`("Expecting HTTP method to be POST")
                .isInstanceOf(HttpPost::class.java)
    }

    @Test
    fun `build put request`() {
        put("https://example.com", client)

        assertThat(requestSlot.captured)
                .`as`("Expecting HTTP method to be PUT")
                .isInstanceOf(HttpPut::class.java)
    }

    @Test
    fun `build delete request`() {
        delete("https://example.com", client)

        assertThat(requestSlot.captured)
                .`as`("Expecting HTTP method to be DELETE")
                .isInstanceOf(HttpDelete::class.java)
    }
}