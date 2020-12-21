package yodamad.day.day18

import yodamad.day.readFileIn

class Puzzle {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println(Puzzle().lines())
        }
    }

    private fun lines() =
        "homework".readFileIn("day18").readLines().map {
            val result = computeLine(it)
            println(result)
            result
        }.sum()

    private fun computeLine(line: String) : Long {
        val cleaned = line.replace(" ", "")
        return if (cleaned.contains("(")) {
            computeWithParenthesis(cleaned)
        } else {
            computeWithNumber(cleaned)
        }
    }

    private fun computeWithParenthesis(line: String) : Long {
        var startIndex = -1
        var newLine: String
        //println(newLine)
        line.mapIndexed { idx, c ->
            when (c) {
                '(' -> startIndex = idx
                ')' -> {
                    val result = computeInner(line, startIndex, idx)
                    newLine = line.replace(line.subSequence(startIndex, idx+1).toString(), result.toString())
                    return computeLine(newLine)
                }
                else -> return@mapIndexed
            }
        }
        return 0
    }

    private fun computeInner(line: String, start: Int, end: Int) = computeWithNumber(line.subSequence(start+1, end).toString())

    private fun computeWithNumber(line: String): Long {
        val elements = mutableListOf<String>()
        var currentNb = ""
        //println(line)
        line.forEach { c ->
            when {
                c.isDigit() -> {
                    currentNb += c
                }
                c == '+' -> {
                    elements.add(currentNb)
                    elements.add("+")
                    currentNb = ""
                }
                c == '*' -> {
                    elements.add(currentNb)
                    elements.add("*")
                    currentNb = ""
                }
            }
        }
        elements.add(currentNb)

        var result = elements[0].toLong()
        elements.forEachIndexed { idx, it ->
            when (it) {
                "+" -> result += elements[idx+1].toInt()
                "*" -> result *= elements[idx+1].toInt()
            }
        }
        return result
    }
}
