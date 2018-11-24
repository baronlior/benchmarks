package com.ni

import com.ni.json.improvedJsonMapper
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class ObjectMapperBenchmark {

  @State(Scope.Thread)
  open class MyState {
    private val raw = listOf("avc230920349", "some_data_to_serialize",
        mapOf("a" to 1, "B" to 2.0, "not" to "too little")
    )
    val mapper = improvedJsonMapper()

    @Param("tiny" , "small", "large") var index : String = ""
    val data = mapOf(
        "tiny" to raw[0]!!,
        "small" to raw,
        "large" to (1..1_000).map { it to raw }
    )

    @TearDown(Level.Trial)
    fun doTearDown() {
      println("data used: $data")
    }
  }

  @Benchmark
  fun newMapper(bh: Blackhole, state: MyState) {
    val mapper = improvedJsonMapper()
    val json = mapper.writeValueAsString(state.data[state.index])
    bh.consume(json)
  }

  @Benchmark
  fun reuseMapper(bh: Blackhole, state: MyState) {
    val json = state.mapper.writeValueAsString(state.data[state.index])
    bh.consume(json)
  }

}
