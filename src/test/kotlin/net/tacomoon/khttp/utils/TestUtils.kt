package net.tacomoon.khttp.utils

import io.mockk.every
import io.mockk.mockk
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.apache.commons.lang3.RandomUtils.nextInt
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.entity.StringEntity

internal fun mockResponse(
        code: Int = nextInt(100, 600),
        body: String? = randomAlphabetic(100)
): CloseableHttpResponse {
    val mock: CloseableHttpResponse = mockk()

    every { mock.entity } answers { if (body == null) null else StringEntity(body) }
    every { mock.statusLine.statusCode } returns code
    every { mock.close() } answers {}

    return mock
}