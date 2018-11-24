package com.ni.json

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue


fun improvedJsonMapper(): ObjectMapper {

  /**
   * IMPORTANT NOTE: improvedJsonMapper should be configured EXACTLY like withKotlin as the default
   * Otherwise some parts of the system will encode in way x, and others try to decode it in way y - a big trouble!
   */

  val result = jacksonObjectMapper() // line per line for debug
  result.withKotlin()

  return result
}


fun ObjectMapper.withKotlin() {
  this.registerModule(KotlinModule()) // Allows using 1. `@JsonProperty` without `:field`, 2. A single constructor (no need to set default values in all fields)
      .registerModule(JavaTimeModule()) // Allow to serialize Java 8 Date/Time API (e.g. LocalDate)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // Allow deserialize json with unknown (= extra) properties
      .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true) // Throw error if primitive types have null
}


fun ObjectMapper.formatJsonString(obj : Any) : String {
  val json = this.readValue<Any>(obj.toString())
  return this.writerWithDefaultPrettyPrinter().writeValueAsString(json)
}
