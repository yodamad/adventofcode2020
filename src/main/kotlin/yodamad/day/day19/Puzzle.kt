package yodamad.day.day19

import yodamad.day.readFileIn

class Puzzle {

    companion object {

        val RAW_RULE = """(\d+): "(\p{Alpha})"""".toRegex()
        val VERY_SIMPLE_RULE = """(\d+): (\d+)""".toRegex()
        val SIMPLE_RULE = """(\d+): (\d+) (\d+)""".toRegex()
        val SIMPLE_PLUS_RULE = """(\d+): (\d+) (\d+) (\d+)""".toRegex()
        val EASY_COMPLEX_RULE = """(\d+): (\d+) \| (\d+)""".toRegex()
        val COMPLEX_RULE = """(\d+): (\d+) (\d+) \| (\d+) (\d+)""".toRegex()

        @JvmStatic
        fun main(args: Array<String>) {
            val puzzle = Puzzle()
            puzzle.computeInput()
            println("Rules")
            //println(puzzle.rules)
            println("========")
            println("Messages")
            //println(puzzle.messages)
            println("========")
            println("Possible values")
            val possible = puzzle.rules[0]?.let { puzzle.authorizedValues(it) }
            //println(possible)
            println("========")
            val goodOnes = puzzle.messages.filter { possible!!.contains(it) }
                .count()
            println(goodOnes)
        }
    }

    val rules = mutableMapOf<Int, Rule>()
    val messages = mutableListOf<String>()

    private fun computeInput() =
        "messages".readFileIn("day19").readLines()
            .filter { it.isNotEmpty() }
            .forEach {
                when {
                    it.contains(":") -> readRule(it)
                    it[0] in setOf('a', 'b') -> messages.add(it)
                }
            }

    private fun readRule(rule: String) {
        when {
            RAW_RULE.matches(rule) -> {
                val values = RAW_RULE.find(rule)!!.groupValues
                rules[values[1].toInt()] = RawRule(values[2].single())
            }
            VERY_SIMPLE_RULE.matches(rule) -> {
                val values = VERY_SIMPLE_RULE.find(rule)!!.groupValues
                rules[values[1].toInt()] = SimpleRule(listOf(values[2].toInt()))
            }
            SIMPLE_RULE.matches(rule) -> {
                val values = SIMPLE_RULE.find(rule)!!.groupValues
                rules[values[1].toInt()] = SimpleRule(listOf(values[2].toInt(), values[3].toInt()))
            }
            SIMPLE_PLUS_RULE.matches(rule) -> {
                val values = SIMPLE_PLUS_RULE.find(rule)!!.groupValues
                rules[values[1].toInt()] = SimpleRule(listOf(values[2].toInt(), values[3].toInt(), values[4].toInt()))
            }
            EASY_COMPLEX_RULE.matches(rule) -> {
                val values = EASY_COMPLEX_RULE.find(rule)!!.groupValues
                val firstPart = SimpleRule(listOf(values[2].toInt()))
                val secondPart = SimpleRule(listOf(values[3].toInt()))
                rules[values[1].toInt()] = ComplexRule(firstPart, secondPart)
            }
            COMPLEX_RULE.matches(rule) -> {
                val values = COMPLEX_RULE.find(rule)!!.groupValues
                val firstPart = SimpleRule(listOf(values[2].toInt(), values[3].toInt()))
                val secondPart = SimpleRule(listOf(values[4].toInt(), values[5].toInt()))
                rules[values[1].toInt()] = ComplexRule(firstPart, secondPart)
            }
        }
    }

    private fun authorizedValues(rule: Rule): MutableList<String> {
        var result = mutableListOf("")
        when {
            rule is RawRule -> run {
                result.forEachIndexed { idx, _ ->
                    result.add(result.removeAt(idx) + rule.char.toString())
                }
            }
            rule is SimpleRule -> rule.indexes.map {
                val newList = mutableListOf<String>()
                authorizedValues(rules[it]!!).forEach {
                    result.forEach { i -> newList.add(i + it) }
                }
                result = newList
            }
            rule is ComplexRule -> {
                result = authorizedValues(rule.first)
                result.addAll(authorizedValues(rule.second))
            }
        }
        // println("$rule = $result")
        return result
    }
}

interface Rule
data class RawRule(val char: Char) : Rule
data class SimpleRule(val indexes : List<Int>) : Rule
data class ComplexRule(val first: SimpleRule, val second: SimpleRule) : Rule
