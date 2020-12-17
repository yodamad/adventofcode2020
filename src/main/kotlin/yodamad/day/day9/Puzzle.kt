package yodamad.day.day9

import yodamad.day.readFileIn

class Puzzle {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Puzzle().findTheBadGuy()
        }
    }

    private val candidates = mutableMapOf<Long, List<Long>>()

    fun findTheBadGuy() {
        val preamble = 25
        val lines = "xmas".readFileIn("day9").readLines()

        for (index in preamble until lines.size) {
            val preambles = mutableListOf<Long>()
            for (backwardIndex in 1 .. preamble) {
                preambles.add(lines[index - backwardIndex].toLong())
            }
            candidates[lines[index].toLong()] = preambles
        }

        val result = candidates.entries.filter { it ->
            it.value.stream()
                .filter{ it2 -> run {
                    val diff = it.key - it2
                    val isHere = it.value.contains(diff)
                    isHere
                } }
                .map { it }
                .count() == 0L
        }.toList()

        println("Bad guy is $result")

        val badValue = result[0].key
        var current = 0L
        var index = 0
        var start = 0
        var end = 0
        var found = false

        while (!found) {
            current = 0
            do {
                current += lines[index].toLong()
                //println("current = $current (badValue is $badValue)")
                if (current == badValue) found = true
                index++
            } while (current < badValue && !found && index < lines.size - 1)

            //println("Start @ $start, End @ $end")
            end = index
            start++
            index = start
        }

        val min = lines.subList(start - 1, end - 1).minOrNull()
        val max = lines.subList(start - 1, end - 1).maxOrNull()
        println("Weakness is $max + $min = ${max!!.toLong() + min!!.toLong()}")
    }
}
