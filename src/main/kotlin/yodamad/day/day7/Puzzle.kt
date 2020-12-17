package yodamad.day.day7

import yodamad.day.readFileIn

class Puzzle {

    val SHINY_GOLD = "shiny gold"

    val allBags = mutableMapOf<String, List<String>>()
    val valuableBags = mutableMapOf<String, Map<String, Int>>()
    var shiny = 0

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Puzzle().computeBags()
        }
    }

    fun computeBags() {
        val input = "bags".readFileIn("day7")

        input.forEachLine {
            l -> run {
                val splitted = l.prepare().split("bag")
                var innerList = mutableListOf<String>()
                var innerMap = mutableMapOf<String, Int>()

                if (l.contains("no other")) allBags[splitted[0]] = listOf()
                else {
                    innerList = mutableListOf()
                    for (index in 1 until splitted.size) {
                        if (splitted[index].isNotEmpty()) {
                            val regex = Regex("(\\d) (\\w+\\s\\w+)").find(splitted[index].trim())!!.destructured
                            innerList.add(regex.component2())
                            innerMap.put(regex.component2(), regex.component1().toInt())
                        }
                    }
                }
                allBags[splitted[0].trim()] = innerList
                valuableBags[splitted[0].trim()] = innerMap
        }
        }

        // Bags containing shiny gold
        for (index in allBags.keys) {
            shiny += betterCheck(allBags[index])
        }
        println(shiny)

        // Nb of bags within shiny
        var nbBags = 0
        allBags[SHINY_GOLD]?.forEach {
            val times = valuableBags[SHINY_GOLD]?.get(it)
            nbBags += times?.plus(times?.times(computeValue(it)))!!
        }
        println(nbBags)
    }

    // Step 1 methods
    private fun betterCheck(innerBags: List<String>?) : Int {
        return if (innerBags?.isNotEmpty() == true && computeAllBags(innerBags).contains(SHINY_GOLD)) {
            1
        } else {
            0
        }
    }

    private fun computeAllBags(innerBags: List<String>?) : List<String> {
        val computeBags = mutableListOf<String>()
        if (innerBags != null && innerBags.isNotEmpty()) {
            computeBags.addAll(innerBags)
            for (element in innerBags) {
                computeBags.addAll(computeAllBags(allBags[element]))
            }
        }
        return computeBags
    }

    // Step 2 methods
    private fun computeValue(element: String) : Int {
        var nbBags = 0
        if (allBags[element].isNullOrEmpty()) return 0
        allBags[element]?.forEach {
            val times = valuableBags[element]?.get(it)
            val nb = computeValue(it)
            nbBags += times?.plus(times?.times(nb))!!
        }
        return nbBags
    }
}

fun String.prepare() = this
    .replace("contain", "")
    .replace("bags", "bag")
    .replace(".", "")
    .replace(",", "")
