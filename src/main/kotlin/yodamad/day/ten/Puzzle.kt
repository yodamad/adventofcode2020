package yodamad.day.ten

import yodamad.day.readFileIn
import java.util.stream.Collectors.toList

class Puzzle {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val puzzle = Puzzle()
            puzzle.findJoltage()
            println(puzzle.solvePart2())
        }
    }

    val fullPath = mutableListOf(0)
    var max = 0

    fun findJoltage() {
        val lines = "joltages".readFileIn("day10").readLines()


        val sortedItems = lines.stream()
            .map { it.toInt() }
            .sorted()
            .collect(toList())

        fullPath.addAll(sortedItems)
        fullPath.add(sortedItems.maxOrNull()!! + 3)
        max = sortedItems.maxOrNull()!! + 3

        var one = 1
        var three = 1

        for (index in 0 until (sortedItems.size - 1)) {

            when(sortedItems[index+1] - sortedItems[index]) {
                1 -> ++one
                3 -> ++three
                else -> continue
            }

        }

        println("Result : $one * $three = ${(one * three)}")
    }

    // Help from https://todd.ginsberg.com/post/advent-of-code/2020/day10/
    // Original solution wasn't not that optimized 😅
    // Use backtracking processing
    fun solvePart2(): Long {
        val pathsForIndex = mutableMapOf(0 to 1L)
        fullPath.drop(1).forEach { index ->
            pathsForIndex[index] = (1 .. 3).map { lookBack ->
                pathsForIndex.getOrDefault(index - lookBack, 0)
            }.sum()
        }
        return pathsForIndex.getValue(fullPath.last())
    }
}
