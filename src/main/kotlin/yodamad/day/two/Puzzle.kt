package yodamad.day.two

import java.io.File

class Puzzle {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println(Puzzle().findPasswords(true))
            println(Puzzle().findPasswords(false))
        }
    }

    fun findPasswords(occurences: Boolean): Int {
        val inputStream = File("./src/main/resources/day2/input.txt").inputStream()
        var valids = 0

        if (occurences) {
            inputStream.bufferedReader().forEachLine {
                if (processLineForOccurences(it)) valids++
            }
        } else {
            inputStream.bufferedReader().forEachLine {
                if (processLineForPosition(it)) valids++
            }
        }

        return valids
    }

    fun processLineForOccurences(line: String): Boolean {
        val match = Regex("(\\d+)-(\\d+) (\\w+): (\\w+)").find(line)!!
        val (strMin, strMax, char, password) = match.destructured
        val occurs = password.count { c -> c == char.single() }

        return occurs >= strMin.toInt() && occurs <= strMax.toInt()
    }

    fun processLineForPosition(line: String): Boolean {
        val match = Regex("(\\d+)-(\\d+) (\\w+): (\\w+)").find(line)!!
        val (strMin, strMax, char, password) = match.destructured

        return (password[strMin.toInt() - 1] == char.single()) xor (password[strMax.toInt() - 1] == char.single())
    }
}
