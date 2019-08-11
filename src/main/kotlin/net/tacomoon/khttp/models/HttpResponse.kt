package net.tacomoon.khttp.models

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.tacomoon.khttp.utils.EntityMapper

data class HttpResponse(val url: String, val code: Int, val body: String) {
    /**
     * Invoke [ObjectMapper.readValue] for mapper configured in [EntityMapper.mapper]
     * Throws same exception as [ObjectMapper.readValue]
     */
    inline fun <reified T> parse(): T {
        return EntityMapper.mapper.readValue(body)
    }
}