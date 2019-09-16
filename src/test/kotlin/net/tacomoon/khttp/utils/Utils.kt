package net.tacomoon.khttp.utils

import io.mockk.every
import io.mockk.mockk
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.RandomUtils
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.entity.StringEntity

internal fun mockResponse(
        code: Int = RandomUtils.nextInt(100, 600),
        body: String = RandomStringUtils.randomAlphabetic(100)
): CloseableHttpResponse {
    val mock: CloseableHttpResponse = mockk()

    every { mock.entity } returns StringEntity(body)
    every { mock.statusLine.statusCode } returns code
    every { mock.close() } answers {}

    return mock
}