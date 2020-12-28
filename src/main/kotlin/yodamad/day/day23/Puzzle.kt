package yodamad.day.day23

import java.time.Instant.now

class Puzzle {
    companion object {
        const val SAMPLE = "389125467"
        const val RESULT10 = "92658374"
        const val RESULT100 = "67384529"
        const val INPUT = "538914762"

        const val CURRENT_INPUT = INPUT
        const val TURNS = 10_000_000
        const val MAX = 1_000_000

        val debug = false

        @JvmStatic
        fun main(args: Array<String>) {
            Puzzle().play()
            // Working but taking 5 hours...
            //Puzzle().playWithCups()
            Puzzle().playInACirle()
        }
    }

    private fun play() {
        var current = SAMPLE
        var currentIdx = 0
        var cup: Int
        var pickup: String
        println(current)
        for (turn in 0 .. 9) {
            println("Start of turn : $current")
            cup = current[currentIdx].toNum()
            pickup = when {
                currentIdx <= (current.length - 4) -> current.subSequence(currentIdx+1, currentIdx+4).toString()
                currentIdx == (current.length - 3) -> current.subSequence(currentIdx+1, currentIdx+3).toString() + current.subSequence(0, 1).toString()
                currentIdx == (current.length - 2) -> current.subSequence(currentIdx+1, current.length).toString() + current.subSequence(0, 2).toString()
                currentIdx == (current.length - 1) -> current.subSequence(0, 3).toString()
                else -> ""
            }
            current = newCurrent(currentIdx, current)

            println("Cup is $cup with pickup $pickup (currently $current)")

            val indexes = current.mapIndexed { index, c -> c.toNum() to index }.sortedByDescending { it.first }
            var nextIndex = indexes.firstOrNull { it.first < cup }?.second
            if (nextIndex == null) nextIndex = indexes.first().second

            println("Destination is ${current[nextIndex]}")

            current = StringBuilder(current).insert(nextIndex+1, pickup).toString()
            currentIdx = current.indexOf(cup.toString()) + 1
            if (currentIdx == current.length) currentIdx = 0
            println("Next start is ${current[currentIdx]}")
        }

        println("Final value is $current")

        val indexOf1 = current.indexOf("1")
        val result = current.mapIndexed { idx, c -> (idx - indexOf1 + 9) % 9 to c }.sortedBy { it.first }.map { it.second }.filter { it != '1' }.joinToString("")
        println(result)
        println(result == RESULT10)
    }

    private fun playWithCups() {
        val current = CURRENT_INPUT.map { c ->  Cup(c.toNum()) }.toMutableList()
        if (!debug) {
            for (turn in 10 .. MAX) {
                current.add(Cup(turn))
            }
        }
        var currentIdx = 0
        var cup: Cup
        var pickup: MutableList<Cup>

        for (turn in 0 until TURNS) {
            if (debug) println("Start of turn : $current")
            if (turn % 10_000 == 0) println("${now()} : Turn $turn ")

            cup = current[currentIdx]!!
            pickup = when {
                currentIdx <= (current.size - 4) -> {
                    val alt = mutableListOf<Cup>()
                    alt.add(current.removeAt(currentIdx+1))
                    alt.add(current.removeAt(currentIdx+1))
                    alt.add(current.removeAt(currentIdx+1))
                    alt
                }
                currentIdx == (current.size - 3) -> {
                    val alt = mutableListOf<Cup>()
                    alt.add(current.removeAt(currentIdx+1))
                    alt.add(current.removeAt(currentIdx+1))
                    alt.add(current.removeAt(0))
                    alt
                }
                currentIdx == (current.size - 2) -> {
                    val alt = mutableListOf<Cup>()
                    alt.add(current.removeAt(currentIdx+1))
                    alt.add(current.removeAt(0))
                    alt.add(current.removeAt(0))
                    alt
                }
                currentIdx == (current.size - 1) -> {
                    val alt = mutableListOf<Cup>()
                    alt.add(current.removeAt(0))
                    alt.add(current.removeAt(0))
                    alt.add(current.removeAt(0))
                    alt
                }
                else -> mutableListOf()
            }

            if (debug) println("Cup is $cup with pickup $pickup. (currently $current)")

            val nextId = nextCupId(cup, pickup)
            val nextIndex = if (nextId == 0) current.indexOf(Cup(currentMax(pickup)))
            else current.indexOf(Cup(nextId))

            current.addAll(nextIndex+1, pickup)
            if (debug) println("Destination is ${current[nextIndex]}. (currently $current)")

            currentIdx = current.indexOf(cup) + 1
            if (currentIdx == current.size) currentIdx = 0
            if (debug) println("Next start is ${current[currentIdx]}")
        }

        if (debug) {
            val flat = current.joinToString("")
            println("Final value is $flat")

            val indexOf1 = flat.indexOf("1")
            val result =
                flat.mapIndexed { idx, c -> (idx - indexOf1 + 9) % 9 to c }.sortedBy { it.first }.map { it.second }
                    .filter { it != '1' }.joinToString("")
            println(result)
            println(result == RESULT100)
        } else {
            val indexOf1 = current.indexOf(Cup(1))
            val result = current[indexOf1+1].id * current[indexOf1+2].id
            println(result)
        }
    }

    private fun playInACirle() {
        val cups = CURRENT_INPUT.map { Cup(it.toNum()) }.toMutableList()

        if (!debug) {
            for (turn in 10 .. MAX) {
                cups.add(Cup(turn))
            }
        }

        val circle = cups.mapIndexed { idx, cup ->
            cup.next = if (idx + 1 == cups.size) cups.first()
            else cups[idx+1]
            cup
        }.sortedBy { it.id }

        var currentCup = cups.first()
        repeat(TURNS) { t ->
            if (t % 10_000 == 0) println("${now()} : Turn $t")
            val pickup = (1 .. 3).runningFold(currentCup) { cup, _ -> cup.next }.drop(1)

            if (debug) println("Current is $currentCup with pickup $pickup (circle = $circle)")

            currentCup.next = pickup.last().next

            val destination = computeNext(currentCup, pickup)
            val memory = circle[destination-1]
            if (debug) println("Memory is $memory")

            pickup.last().next = memory.next
            memory.next = pickup.first()
            currentCup = currentCup.next

            if (debug) println("Circle = $circle")
        }

        if (debug) {
            val flat = StringBuffer()
            var tmp = circle.find { it.id == 1 }!!
            while (tmp.next.id != 1) {
                flat.append(circle.find { it.id == tmp.next.id }!!.id)
                tmp = tmp.next
            }
            println("Final value is $flat")

            val indexOf1 = flat.indexOf("1")
            val result =
                flat.mapIndexed { idx, c -> (idx - indexOf1 + 9) % 9 to c }.sortedBy { it.first }.map { it.second }
                    .filter { it != '1' }.joinToString("")
            println(result)
            println(result == RESULT100)
        } else {
            val one = circle.find { it.id == 1 }!!
            val first = circle.find { it.id == one.next.id }!!
            val second = circle.find { it.id == first.next.id }!!

            val result: Long = first.id.toLong() * second.id.toLong()
            println("Result is $result")
        }

    }

    private fun nextCupId(current: Cup, pickup: List<Cup>): Int {
        var nextId = current.id - 1
        while (pickup.contains(Cup(nextId)) && nextId > 0) {
            nextId--
        }
        return nextId
    }

    private fun computeNext(current: Cup, pickup: List<Cup>): Int {
        var nextId = current.id - 1
        while (pickup.contains(Cup(nextId)) && nextId > 0) {
            nextId--
        }
        if (nextId == 0) {
            nextId = MAX
            while (pickup.contains(Cup(nextId))) {
                nextId--
            }
        }
        return nextId
    }

    private fun currentMax(pickup: List<Cup>): Int {
        var max = MAX
        while (pickup.contains(Cup(max))) {
            max--
        }
        return max
    }

    private fun newCurrent(currentIdx: Int, current: String) = when {
        currentIdx <= (current.length - 4) -> {
            current.removeRange(currentIdx+1, currentIdx+4)
        }
        currentIdx <= (current.length - 3) -> {
            current.removeRange(currentIdx+1, currentIdx+3).removeRange(0, 1)
        }
        currentIdx <= (current.length - 2) -> {
            current.removeRange(currentIdx+1, current.length).removeRange(0, 2)
        }
        currentIdx <= (current.length - 1) -> {
            current.removeRange(0, 3)
        }
        else -> ""
    }
}

fun Char.toNum() = Character.getNumericValue(this)

data class Cup(val id: Int) {

    lateinit var next: Cup

    override fun toString(): String {
        return "$id (${next.id})"
    }

    override fun equals(other: Any?): Boolean {
        return other is Cup && other.id == id
    }

    override fun hashCode(): Int {
        return id
    }
}
