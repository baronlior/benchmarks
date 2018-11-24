package com.ni

import com.fasterxml.jackson.databind.ObjectMapper
import com.ni.json.improvedJsonMapper
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class ObjectMapperBenchmark {

  @State(Scope.Benchmark)
  open class MyState {

    lateinit var data : Any
    lateinit var mapper: ObjectMapper

    @Param("tiny" , "small", "large") var index : String = ""

    @Setup(Level.Trial)
    fun setup() {
      val raw = listOf("avc230920349", "some_data_to_serialize",
          mapOf("a" to 1, "B" to 2.0, "not" to "too little")
      )
      this.data = mapOf(
          "tiny" to raw[0]!!,
          "small" to raw,
          "large" to (1..1_000).map { it to raw }
      )[index]!!
      this.mapper = improvedJsonMapper()
    }

    @TearDown(Level.Trial)
    fun doTearDown() {
      println("data used: $data")
    }
  }

  @Benchmark
  fun newMapper(bh: Blackhole, state: MyState) {
    val mapper = improvedJsonMapper()
    val json = mapper.writeValueAsString(state.data)
    bh.consume(json)
  }

  @Benchmark
  fun reuseMapper(bh: Blackhole, state: MyState) {
    val json = state.mapper.writeValueAsString(state.data)
    bh.consume(json)
  }

}
