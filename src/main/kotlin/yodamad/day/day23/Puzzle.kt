package yodamad.day.day23

class Puzzle {
    companion object {
        const val SAMPLE = "389125467"
        const val RESULT10 = "92658374"
        const val RESULT100 = "67384529"
        const val INPUT = "538914762"

        @JvmStatic
        fun main(args: Array<String>) {
            Puzzle().play()
            Puzzle().playWithCups()
        }
    }

    private fun play() {
        var current = SAMPLE
        var currentIdx = 0
        var cup: Int
        var pickup: String
        println(current)
        for (turn in 0 .. 99) {
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
        println(result == RESULT100)
    }

    private fun playWithCups() {
        var current = SAMPLE.map { c ->  Cup(c.toNum()) }.toMutableList()
        var currentIdx = 0
        var cup: Cup
        var pickup: MutableList<Cup>

        for (turn in 0 .. 99) {
            println("Start of turn : $current")
            cup = current[currentIdx]!!
            pickup = when {
                currentIdx <= (current.size - 4) -> current.filterIndexed { index, _ -> index in currentIdx+1 .. currentIdx+3 }.toMutableList()
                currentIdx == (current.size - 3) -> {
                    val alt = current.filterIndexed { index, _ -> index in listOf(currentIdx+1, currentIdx+2) }.toMutableList()
                    alt.add(current.filterIndexed { index, _ -> index == 0 }.first())
                    alt
                }
                currentIdx == (current.size - 2) -> {
                    val alt = current.filterIndexed { index, _ -> index == currentIdx+1 }.toMutableList()
                    alt.addAll(current.filterIndexed { index, _ -> index in 0 .. 1 }.toMutableList())
                    alt
                }
                currentIdx == (current.size - 1) -> current.filterIndexed { index, _ -> index in 0 .. 2 }.toMutableList()
                else -> mutableListOf()
            }
            current = (current subtract pickup).toMutableList()

            println("Cup is $cup with pickup $pickup. (currently $current)")

            var nextIndex = current.mapIndexed { idx, cup -> idx to cup }.sortedByDescending { it.second.id }.firstOrNull { it.second.id < cup.id }?.first
            if (nextIndex == -1 || nextIndex == null) nextIndex = current.mapIndexed { idx, cup -> idx to cup }.maxByOrNull { it.second.id }!!.first

            current.addAll(nextIndex+1, pickup)
            println("Destination is ${current[nextIndex]}. (currently $current)")

            currentIdx = current.indexOf(cup) + 1
            if (currentIdx == current.size) currentIdx = 0
            println("Next start is ${current[currentIdx]}")
        }

        val flat = current.joinToString("")
        println("Final value is $flat")

        val indexOf1 = flat.indexOf("1")
        val result = flat.mapIndexed { idx, c -> (idx - indexOf1 + 9) % 9 to c }.sortedBy { it.first }.map { it.second }.filter { it != '1' }.joinToString("")
        println(result)
        println(result == RESULT100)
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
    override fun toString(): String {
        return id.toString()
    }

    override fun equals(other: Any?): Boolean {
        return other is Cup && other.id == id
    }
}
