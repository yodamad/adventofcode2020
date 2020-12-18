package yodamad.day.day16

import yodamad.day.readFileIn

class Puzzle {
    companion object {
        val regex = """(\w+): (\d+-\d+) or (\d+-\d+)""".toRegex()
        @JvmStatic
        fun main(args: Array<String>) {
            Puzzle().readInput()
        }
    }

    lateinit var myTicket: String
    var ticketsIndex = 0

    var ranges = mutableListOf<Range>()
    val nearbyTickets = mutableListOf<Int>()

    fun readInput() {
        val lines = "seats".readFileIn("day16").readLines()

        lines.forEachIndexed { idx, l -> run {
            when {
                l.isEmpty() -> {
                    // Skip
                }
                l.contains("or") -> {
                    val values = regex.find(l)!!.groupValues
                    computeRange(values[2])
                    computeRange(values[3])
                }
                l == "your ticket:" -> {
                    myTicket = lines[idx+1]
                }
                l == "nearby tickets:" -> {
                    ticketsIndex = idx
                }
                idx > ticketsIndex -> {
                    nearbyTickets.addAll(l.split(",").map { it.toInt() })
                }
            }
        }
        }
        println("Ranges are $ranges")

        val errorRate = nearbyTickets.filter { !isAuthorized(it) }
            .sum()
        println("Error rate = $errorRate")
    }

    private fun computeRange(input: String) {
        val min = input.split("-")[0].toInt()
        val max = input.split("-")[1].toInt()

       if (ranges.find { IntRange(it.min, it.max).contains(min) || IntRange(it.min, it.max).contains(max) } != null) {
            ranges.filter { IntRange(it.min, it.max).contains(min) || IntRange(it.min, it.max).contains(max) }
                .map {
                    if ( it.min > min) it.min = min
                    else if (it.max < max) it.max = max
                }
        } else {
            ranges.add(Range(min, max))
        }
    }

    private fun isAuthorized(value: Int) : Boolean = ranges.find { it.contains(value) } != null

}

data class Range(var min: Int, var max: Int) {
    fun contains(value: Int) = IntRange(min, max).contains(value)
}
