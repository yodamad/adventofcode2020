package yodamad.day.day18

import yodamad.day.readFileIn

class Puzzle {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println(Puzzle().lines())
            //println(Puzzle().computeLine("2 * 3 + (4 * 5)"))
        }
    }

    val part1 = false

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
        //println(line)
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

        if (part1) return computePart1(elements)
        return computePart2(elements)
    }

    private fun computePart1(elements: List<String>) : Long {
        var result = elements[0].toLong()
        elements.forEachIndexed { idx, it ->
            when (it) {
                "+" -> result += elements[idx+1].toInt()
                "*" -> result *= elements[idx+1].toInt()
            }
        }
        return result
    }

    private fun computePart2(elements: List<String>) : Long {
        val computed = computePlus(elements)
        var result = computed[0].toLong()
        computed.forEachIndexed { idx, it ->
            when (it) {
                "*" -> result *= computed[idx+1].toInt()
            }
        }
        return result
    }

    private fun computePlus(elements: List<String>) : List<String> {
        val tmpList = mutableListOf<String>()
        elements.forEachIndexed { idx, s ->
            when (s) {
                "*" -> {
                    tmpList.add("*")
                }
                "+" -> {
                    val sum = elements[idx-1].toLong() + elements[idx+1].toLong()
                    val newList = mutableListOf<String>()
                    newList.addAll(tmpList)
                    newList.add(sum.toString())
                    newList.addAll(elements.subList(idx+2, elements.size))
                    return if (newList.contains("+")) computePlus(newList)
                    else newList
                }
                else -> {
                    if ((idx+1) < elements.size && elements[idx+1] != "+") tmpList.add(s)
                    else if (idx+1 == elements.size) tmpList.add(s)
                }
            }
        }
        return tmpList
    }
}
