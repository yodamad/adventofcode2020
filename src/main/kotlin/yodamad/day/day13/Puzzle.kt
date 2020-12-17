package yodamad.day.day13

import yodamad.day.readFileIn

class Puzzle {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Puzzle().findBus()
            //Puzzle().findTimestamp()
            Puzzle().optimizedTimestamp()
        }
    }

    fun findBus() {
        val lines = "schedule".readFileIn("day13").readLines()
        val busId = lines[0].toInt()

        val busLines = lines[1].split(",").filter { it != "x" }
            .map { it.toInt() }
            .sorted()

        println("Bus lines $busLines")

        val bus = busLines.mapIndexed { index: Int, i: Int -> Pair(index, i - busId.rem(i)) }
            .sortedBy { it.second }
            .minByOrNull { it.second }

        println("Bus ID is ${busLines[bus!!.first]}")
        println("so result is ${busLines[bus.first]} * ${bus.second} = ${(busLines[bus.first] * bus.second)}")
    }

    var initialBusId : Int = 0
    lateinit var busses : List<Pair<Long, Int>>

    // Working but not optimized for large numbers
    fun findTimestamp() {
        val lines = "schedule".readFileIn("day13").readLines()

        initialBusId = lines.last().split(",").first().toInt()

        busses = lines.last().split(",")
            .mapIndexed { idx, busId -> Pair(busId, idx) }
            .filter { it.first != "x" }
            .map { Pair(it.first.toLong(), it.second)}
            .drop(1)
            .sortedBy { it.first }
            .reversed()

        println("Initial bus is $initialBusId and buses $busses")

        var found = false
        // As explained in the instructions
        var currentOffset = 1000000000000000L

        var power = currentOffset / busses[0].first

        do {
            currentOffset = (busses[0].first * power - busses[0].second)
            if (currentOffset.rem(initialBusId) == 0L) {
                found = checkOther(1, currentOffset)
            }
            power++
        } while (!found)

        println("Result is $currentOffset")
    }

    private tailrec fun checkOther(index: Int, timestamp: Long) : Boolean {
        println("Try index $index")
        if (index == busses.size) return true
        val busInfo = busses[index]
        return if ((timestamp + busInfo.second).rem(busInfo.first) != 0L) false
            else checkOther(index + 1, timestamp)
    }

    // Optimized, help by https://todd.ginsberg.com/post/advent-of-code/2020/day13/
    fun optimizedTimestamp() : Long {
        val lines = "schedule".readFileIn("day13").readLines()
        val indexedBusses: List<IndexedBus> = lines
            .last()
            .split(",")
            .mapIndexedNotNull { index, s -> if (s == "x") null else IndexedBus(index, s.toLong()) }

        var stepSize = indexedBusses.first().bus
        var time = 0L
        indexedBusses.drop(1).forEach { (offset, bus) ->
            while ((time + offset) % bus != 0L) {
                time += stepSize
            }
            stepSize *= bus
        }
        println("Time is $time")
        return time
    }
}

data class IndexedBus(val index: Int, val bus: Long)
