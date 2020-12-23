package yodamad.day.day21

import yodamad.day.readFileIn

class Puzzle {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val puzzle = Puzzle()
            puzzle.readList()
            //println("Foods = ${puzzle.mappings}")
            //val notAllergens = puzzle.findAll()
            val result = puzzle.findDuo()
            //println("Possibilities = ${puzzle.possibilities}")
            println("My Result is $result")
        }
    }

    var mappings = mapOf<MutableList<String>, MutableList<String>>()

    private fun readList() {
       val lines = "foods".readFileIn("day21").readLines()
       mappings = lines.map {
           val foods = it.substringBefore(" (").split(" ").toMutableList()
           val allergies = it.substringAfter(" (contains ").substringBefore(")").split(", ").toMutableList()
           foods to allergies
       }.toMap()
    }

    var possibilities = mutableMapOf<String, MutableList<String>>()
    var found = mutableListOf<String>()

    // part1
    private fun findAll(): Int {
        mappings.map { entry ->
            entry.value.forEach { run {
                if (!possibilities.keys.contains(it)) {
                    possibilities[it] = entry.key
                } else {
                    val filter = possibilities[it]?.intersect(entry.key)?.toMutableList()
                    possibilities[it] = filter!!
                }
                if (possibilities[it]?.size == 1) found.add(possibilities[it]?.get(0)!!)
            } }
        }
        val foundWithAllergens = possibilities.values.flatten().toMutableList()
        return mappings.keys.flatten().filter { !foundWithAllergens.contains(it) }.toMutableList().size
    }

    private fun findDuo(): String {
        mappings.map { entry ->
            entry.value.forEach { run {
                if (!possibilities.keys.contains(it)) {
                    possibilities[it] = entry.key
                } else {
                    val filter = possibilities[it]?.intersect(entry.key)?.toMutableList()
                    possibilities[it] = filter!!
                }
                //possibilities[it] = possibilities[it]?.filter { it1 -> !found.contains(it1) }!!.toMutableList()

                if (possibilities[it]?.size == 1) found.add(possibilities[it]?.get(0)!!)
            } }
        }

        do {
            val allFound = possibilities
                .filter { it.value.size > 1 }
                .map { entry ->
                    possibilities[entry.key] = (entry.value subtract found).toMutableList()
                }.isEmpty()

            found = possibilities.values.filter { it.size == 1 }.map { it[0] }.toMutableList()
        } while (!allFound)

        val foundWithAllergens = possibilities.toSortedMap().values.flatten().distinct().toMutableList()
        return foundWithAllergens.joinToString(",")
    }
}
