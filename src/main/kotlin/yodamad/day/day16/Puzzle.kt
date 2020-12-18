package yodamad.day.day16

import yodamad.day.readFileIn

class Puzzle {
    companion object {
        val regex = """(\w+\p{Blank}*\w*): (\d+-\d+) or (\d+-\d+)""".toRegex()
        @JvmStatic
        fun main(args: Array<String>) {
            Puzzle().readInput()
        }
    }

    lateinit var myTicket: String
    var ticketsIndex = 0

    var ranges = mutableListOf<Range>()
    var rawRanges = mutableListOf<LabelledRange>()
    val splittedTickets = mutableListOf<Int>()
    val nearbyTickets = mutableListOf<String>()
    val mappings = mutableMapOf<Int, MutableSet<String>>()
    val indexesFound = mutableMapOf<String, Int>()

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
                    computeRawRange(values[1], values[2], values[3])
                }
                l == "your ticket:" -> {
                    myTicket = lines[idx+1]
                    findMappings(lines[idx+1], this::findRanges)
                }
                l == "nearby tickets:" -> {
                    ticketsIndex = idx
                }
                idx > ticketsIndex -> {
                    nearbyTickets.add(l)
                    splittedTickets.addAll(l.split(",").map { it.toInt() })
                }
            }
        }
        }
        println("Ranges are $ranges")
        println("Raw ranges are $rawRanges")

        val errorRate = splittedTickets.filter { !isAuthorized(it) }.sum()
        println("Error rate = $errorRate")

        nearbyTickets.filter { areAuthorized(it) }.map { findMappings(it, this::findMatchingRanges) }

        println("Mappings are $mappings")

        elections()
        var result = 1L
        indexesFound.forEach { (s, i) ->
            if (s.startsWith("departure")) {
                result *= myTicket.split(",")[i].toLong()
            }
        }
        println("Result is $result")
    }

    private fun elections() {
        var allElected: Boolean
        do {
            mappings
                .map {
                    it.value.removeAll(indexesFound.keys)
                    it
                }
                .filter { it.value.size == 1 }
                .filter { !indexesFound.contains(it.value) }
                .forEach {
                    it.value.forEach { idx ->
                        indexesFound.put(idx, it.key)
                    }
                }
            allElected = mappings.values.all { it.size == 0 }
        } while (!allElected)
        println(indexesFound)
    }

    private fun findMappings(elements: String, f: (index: Int, element: String) -> Unit) {
        elements.split(",")
            .mapIndexed { idx, s -> f(idx, s) }
    }

    private fun findRanges(index: Int, element: String) {
        rawRanges.filter { IntRange(it.ranges.first.min, it.ranges.first.max).contains(element.toInt())
                || IntRange(it.ranges.second.min, it.ranges.second.max).contains(element.toInt()) }
            .filter { !mappingsContains(index, it.label) }
            .map {
                if (mappings[index] == null) mappings[index] = mutableSetOf()
                mappings[index]?.add(it.label)
            }
    }

    private fun findMatchingRanges(index: Int, element: String) {
        val matching = rawRanges.filter { IntRange(it.ranges.first.min, it.ranges.first.max).contains(element.toInt())
                || IntRange(it.ranges.second.min, it.ranges.second.max).contains(element.toInt()) }
            .map { it.label }

        val found = mappings[index]
        if (found != null) {
            mappings[index] = (found intersect matching) as MutableSet<String>
        }
    }

    private fun mappingsContains(index: Int, label: String) = mappings[index] != null && mappings[index]!!.contains(label)

    private fun computeRawRange(label: String, first: String, second: String) {
        var min = first.split("-")[0].toInt()
        var max = first.split("-")[1].toInt()
        val firstRg = Range(min, max)
        min = second.split("-")[0].toInt()
        max = second.split("-")[1].toInt()
        val secondRg = Range(min, max)

        rawRanges.add(LabelledRange(label, Pair(firstRg, secondRg)))
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

    private fun areAuthorized(ticket: String) : Boolean = ticket.split(",").all { isAuthorized(it.toInt()) }

    private fun isAuthorized(value: Int) : Boolean = ranges.find { it.contains(value) } != null

}

data class LabelledRange(val label: String, val ranges: Pair<Range, Range>)

data class Range(var min: Int, var max: Int) {
    fun contains(value: Int) = IntRange(min, max).contains(value)
}
