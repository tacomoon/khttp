package net.tacomoon

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object ResponseParser {
    val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()
}

data class HttpResponse(val url: String, val code: Int, val body: String) {
    inline fun <reified T> parse(): T {
        return ResponseParser.mapper.readValue(body)
    }
}