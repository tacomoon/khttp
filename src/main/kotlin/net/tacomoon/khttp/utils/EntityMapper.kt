package net.tacomoon.khttp.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object EntityMapper {
    val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()
}