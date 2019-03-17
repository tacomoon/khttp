package net.tacomoon

import io.mockk.every
import io.mockk.mockk
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.entity.StringEntity

internal fun mockResponse(code: Int = 200, body: String = ""): CloseableHttpResponse {
    val mock: CloseableHttpResponse = mockk()

    every { mock.statusLine.statusCode } returns code
    every { mock.entity } returns StringEntity(body)
    every { mock.close() } answers {}

    return mock
}