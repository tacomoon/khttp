package net.tacomoon

import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.slot
import io.mockk.spyk
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class HttpRequestBuilderMethodTest {

    private val client: CloseableHttpClient = spyk(HttpClients.createDefault())
    private val slot: CapturingSlot<HttpRequestBase> = slot()

    private val url = "https://example.com"

    @BeforeEach
    internal fun beforeEach() {
        every { client.execute(capture(slot)) } answers { mockResponse() }
    }

    @Test
    fun `build get request`() {
        request {
            client(client)
            get(url)
        }

        assertThat(slot.captured)
                .`as`("Expecting http method to be GET")
                .isInstanceOf(HttpGet::class.java)
    }

    @Test
    fun `build post request`() {
        request {
            client(client)
            post(url)
        }

        assertThat(slot.captured)
                .`as`("Expecting http method to be POST")
                .isInstanceOf(HttpPost::class.java)
    }

    @Test
    fun `build put request`() {
        request {
            client(client)
            put(url)
        }

        assertThat(slot.captured)
                .`as`("Expecting http method to be PUT")
                .isInstanceOf(HttpPut::class.java)
    }

    @Test
    fun `build delete request`() {
        request {
            client(client)
            delete(url)
        }

        assertThat(slot.captured)
                .`as`("Expecting http method to be DELETE")
                .isInstanceOf(HttpDelete::class.java)
    }
}