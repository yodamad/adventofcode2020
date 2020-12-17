package yodamad.day.day6

import java.io.File
import java.io.InputStream

class Puzzle {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            //println(Puzzle().readAnswers())
            println(Puzzle().readCorrectAnswers())
        }
    }

    private fun getInput() : InputStream = File("./src/main/resources/day6/answers").inputStream()

    fun readCorrectAnswers() : Int {

        val answers = mutableListOf<Set<String>>()
        var group = setOf<String>()
        var initialized = false

        getInput().bufferedReader().lines().forEach {
                l -> run {
            if (l.isEmpty()) {
                // End of passport, initialize a new one
                answers.add(group)
                println(group)
                group = setOf()
                initialized = false
            } else {
                if (group.isEmpty() && !initialized) {
                    group = l.map { it.toString() }.toSet()
                    initialized = true
                } else {
                    group = l.map { it.toString() }.toSet() intersect group
                }
            }
        }
        }

        println(answers)
        return answers.map { it.size }.sum()
    }

    fun readAnswers(): Int {
        val answers = mutableListOf<List<String>>()
        var group = mutableListOf<String>()

        getInput().bufferedReader().lines().forEach {
                l -> run {
                    if (l.isEmpty()) {
                        // End of passport, initialize a new one
                        answers.add(group)
                        println(group)
                        group = mutableListOf()
                    } else {
                        // Retrieve only key part
                        if (l.length == 1 && !group.contains(l)) { group.add(l) }
                        else if (l.length != 1) {
                            l.toList().forEach {
                                if (!group.contains(it.toString())) group.add(it.toString())
                            }
                        }
                    }
                }
        }

        println(answers)
        return answers.map { it.size }.sum()
    }
}
