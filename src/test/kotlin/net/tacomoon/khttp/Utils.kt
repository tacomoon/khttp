package net.tacomoon.khttp

import io.mockk.every
import io.mockk.mockk
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.entity.StringEntity

internal fun mockResponse(code: Int = 200, body: String = ""): CloseableHttpResponse {
    val mock: CloseableHttpResponse = mockk()

    every { mock.entity } returns StringEntity(body)
    every { mock.statusLine.statusCode } returns code
    every { mock.close() } answers {}

    return mock
}