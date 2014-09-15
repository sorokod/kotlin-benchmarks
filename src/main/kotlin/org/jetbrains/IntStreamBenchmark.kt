package org.jetbrains

import org.openjdk.jmh.annotations.*
import java.util.concurrent.*
import org.openjdk.jmh.infra.Blackhole
import java.util.ArrayList

BenchmarkMode(Mode.AverageTime)
OutputTimeUnit(TimeUnit.NANOSECONDS)
open class IntStreamBenchmark : SizedBenchmark() {
    private var _data: Iterable<Int>? = null
    val data: Iterable<Int>
        get() = _data!!

    Setup fun setup() {
        _data = intValues(size)
    }


    CompilerControl(CompilerControl.Mode.DONT_INLINE)
    Benchmark fun copy(): List<Int> {
        return data.stream().toList()
    }

    CompilerControl(CompilerControl.Mode.DONT_INLINE)
    Benchmark fun copySequence(): List<Int> {
        return data.sequence().toList()
    }

    CompilerControl(CompilerControl.Mode.DONT_INLINE)
    Benchmark fun copyManual(): List<Int> {
        val list = ArrayList<Int>()
        for (item in data.stream()) {
            list.add(item)
        }
        return list
    }

    CompilerControl(CompilerControl.Mode.DONT_INLINE)
    Benchmark fun filterAndCount(): Int {
        return data.stream().filter { filterLoad(it) }.count()
    }

    CompilerControl(CompilerControl.Mode.DONT_INLINE)
    Benchmark fun filterAndMap(bh: Blackhole) {
        for (item in data.stream().filter { filterLoad(it) }.map { mapLoad(it) })
            bh.consume(item)
    }

    CompilerControl(CompilerControl.Mode.DONT_INLINE)
    Benchmark fun filterAndMapSequence(bh: Blackhole) {
        for (item in data.sequence().filter { filterLoad(it) }.map { mapLoad(it) })
            bh.consume(item)
    }

    CompilerControl(CompilerControl.Mode.DONT_INLINE)
    Benchmark fun filterAndMapManual(bh: Blackhole) {
        for (it in data.stream()) {
            if (filterLoad(it)) {
                val item = mapLoad(it)
                bh.consume(item)
            }
        }
    }

    CompilerControl(CompilerControl.Mode.DONT_INLINE)
    Benchmark fun filter(bh: Blackhole) {
        for (item in data.stream().filter { filterLoad(it) })
            bh.consume(item)
    }

    CompilerControl(CompilerControl.Mode.DONT_INLINE)
    Benchmark fun filterSequence(bh: Blackhole) {
        for (item in data.sequence().filter { filterLoad(it) })
            bh.consume(item)
    }

    CompilerControl(CompilerControl.Mode.DONT_INLINE)
    Benchmark fun filterManual(bh: Blackhole){
        for (it in data.stream()) {
            if (filterLoad(it))
                bh.consume(it)
        }
    }

    CompilerControl(CompilerControl.Mode.DONT_INLINE)
    Benchmark fun countFilteredManual(): Int {
        var count = 0
        for (it in data.stream()) {
            if (filterLoad(it))
                count++
        }
        return count
    }

    CompilerControl(CompilerControl.Mode.DONT_INLINE)
    Benchmark fun countFiltered(): Int {
        return data.stream().count { filterLoad(it) }
    }

    CompilerControl(CompilerControl.Mode.DONT_INLINE)
    Benchmark fun countFilteredLocal(): Int {
        return data.stream().cnt { filterLoad(it) }
    }

    CompilerControl(CompilerControl.Mode.DONT_INLINE)
    Benchmark fun reduce(): Int {
        return data.stream().fold(0) {(acc, it) -> if (filterLoad(it)) acc + 1 else acc }
    }
}