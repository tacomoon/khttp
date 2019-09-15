package net.tacomoon.khttp.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object EntityMapper {
    val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()

    fun <T> serialize(value: T): String {
        return mapper.writeValueAsString(value)
    }

    inline fun <reified T> deserialize(value: String): T {
        return mapper.readValue(value)
    }
}