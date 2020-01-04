package net.tacomoon.khttp.model

import com.fasterxml.jackson.databind.ObjectMapper
import net.tacomoon.khttp.mapper.EntityMapper

data class HttpResponse(val url: String, val code: Int, val body: String) {
    /**
     * Invoke [ObjectMapper.readValue] for mapper configured in [EntityMapper.mapper]
     * Throws same exceptions as [ObjectMapper.readValue]
     */
    inline fun <reified T> parse(): T {
        return EntityMapper.deserialize(body)
    }
}