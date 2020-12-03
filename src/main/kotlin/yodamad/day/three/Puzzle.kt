package yodamad.day.three

import java.io.File

class Puzzle {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val case11 = Puzzle().computeTrees(1, 1)
            val case31 = Puzzle().computeTrees(3, 1)
            val case51 = Puzzle().computeTrees(5, 1)
            val case71 = Puzzle().computeTrees(7, 1)
            val case12 = Puzzle().computeTrees(1, 2)

            println(case11 * case31 * case51 * case71 * case12)
        }
    }

    fun computeTrees(right: Int, bottom: Int): Int {
        val inputStream = File("./src/main/resources/day3/map1").inputStream()

        val lines = inputStream.bufferedReader().readLines()

        var index = right
        var trees = 0

        println(lines.size)
        var lineIndex = 0

        do {
            lineIndex += bottom
            if (lines[lineIndex][index] == '#') {
                println("Found in line $lineIndex at $index")
                trees++
            }
            index = (index + right) % lines[lineIndex].length
        } while (lineIndex < (lines.size - 1))

        return trees
    }
}
