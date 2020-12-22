package yodamad.day.day19

import yodamad.day.readFileIn

class Puzzle {

    companion object {

        val RAW_RULE = """(\d+): "(\p{Alpha})"""".toRegex()
        val VERY_SIMPLE_RULE = """(\d+): (\d+)""".toRegex()
        val SIMPLE_RULE = """(\d+): (\d+) (\d+)""".toRegex()
        val SIMPLE_PLUS_RULE = """(\d+): (\d+) (\d+) (\d+)""".toRegex()
        val EASY_COMPLEX_RULE = """(\d+): (\d+) \| (\d+)""".toRegex()
        val MIXED_COMPLEX_RULE = """(\d+): (\d+) \| (\d+) (\d+)""".toRegex()
        val COMPLEX_RULE = """(\d+): (\d+) (\d+) \| (\d+) (\d+)""".toRegex()
        val VERY_COMPLEX_RULE = """(\d+): (\d+) (\d+) \| (\d+) (\d+) (\d+)""".toRegex()

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
        "messages2".readFileIn("day19").readLines()
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
                rules[values[1].toInt()] = RawRule(values[1].toInt(), values[2].single())
            }
            VERY_SIMPLE_RULE.matches(rule) -> {
                val values = VERY_SIMPLE_RULE.find(rule)!!.groupValues
                rules[values[1].toInt()] = SimpleRule(values[1].toInt(), listOf(values[2].toInt()))
            }
            SIMPLE_RULE.matches(rule) -> {
                val values = SIMPLE_RULE.find(rule)!!.groupValues
                rules[values[1].toInt()] = SimpleRule(values[1].toInt(), listOf(values[2].toInt(), values[3].toInt()))
            }
            SIMPLE_PLUS_RULE.matches(rule) -> {
                val values = SIMPLE_PLUS_RULE.find(rule)!!.groupValues
                rules[values[1].toInt()] = SimpleRule(values[1].toInt(), listOf(values[2].toInt(), values[3].toInt(), values[4].toInt()))
            }
            EASY_COMPLEX_RULE.matches(rule) -> {
                val values = EASY_COMPLEX_RULE.find(rule)!!.groupValues
                val firstPart = SimpleRule(-1, listOf(values[2].toInt()))
                val secondPart = SimpleRule(-1, listOf(values[3].toInt()))
                rules[values[1].toInt()] = ComplexRule(values[1].toInt(), firstPart, secondPart)
            }
            MIXED_COMPLEX_RULE.matches(rule) -> {
                val values = MIXED_COMPLEX_RULE.find(rule)!!.groupValues
                val firstPart = SimpleRule(-1, listOf(values[2].toInt()))
                val secondPart = SimpleRule(-1, listOf(values[3].toInt(), values[4].toInt()))
                rules[values[1].toInt()] = ComplexRule(values[1].toInt(), firstPart, secondPart)
            }
            COMPLEX_RULE.matches(rule) -> {
                val values = COMPLEX_RULE.find(rule)!!.groupValues
                val firstPart = SimpleRule(-1, listOf(values[2].toInt(), values[3].toInt()))
                val secondPart = SimpleRule(-1, listOf(values[4].toInt(), values[5].toInt()))
                rules[values[1].toInt()] = ComplexRule(values[1].toInt(), firstPart, secondPart)
            }
            VERY_COMPLEX_RULE.matches(rule) -> {
                val values = VERY_COMPLEX_RULE.find(rule)!!.groupValues
                val firstPart = SimpleRule(-1, listOf(values[2].toInt(), values[3].toInt()))
                val secondPart = SimpleRule(-1, listOf(values[4].toInt(), values[5].toInt(), values[6].toInt()))
                rules[values[1].toInt()] = ComplexRule(values[1].toInt(), firstPart, secondPart)
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
data class RawRule(val id: Int, val char: Char) : Rule
data class SimpleRule(val id: Int, val indexes : List<Int>) : Rule
data class ComplexRule(val id: Int, val first: SimpleRule, val second: SimpleRule) : Rule
