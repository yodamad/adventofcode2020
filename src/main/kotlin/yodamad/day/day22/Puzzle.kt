package yodamad.day.day22

import yodamad.day.readFileIn

class Puzzle {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("==== Part 1 ====")
            var puzzle = Puzzle()
            puzzle.distribution()
            println(puzzle.player1)
            println(puzzle.player2)
            puzzle.play()
            println(puzzle.player1)
            println(puzzle.player2)
            println("Score is ${puzzle.score()}")
            println("==== ====== ====")
            println("==== Part 2 ====")
            puzzle = Puzzle()
            puzzle.distribution()
            println(puzzle.player1)
            println(puzzle.player2)
            puzzle.recursivePlay(puzzle.player1, puzzle.player2)
            println(puzzle.player1)
            println(puzzle.player2)
            println("Score is ${puzzle.score()}")
        }
    }

    val player1 = mutableListOf<Int>()
    val player2 = mutableListOf<Int>()
    private val input = "cards"

    private fun distribution() {
        val lines = input.readFileIn("day22").readLines()
        lines.takeWhile{ it.isNotBlank() }
            .filter{ !it.contains("Player") }
            .map { player1.add(it.toInt()) }

        val init2 = lines.indexOf("Player 2:")
        lines.subList(init2+1, lines.size).takeWhile{ it.isNotBlank() }
            .map { player2.add(it.toInt()) }
    }

    private fun play() {
        do {
            val card1 = player1.removeAt(0)
            val card2 = player2.removeAt(0)
            when {
                card1 > card2 -> player1.addAll(listOf(card1, card2))
                card2 > card1 -> player2.addAll(listOf(card2, card1))
            }
        } while (player1.isNotEmpty() && player2.isNotEmpty())
    }

    private fun score() : Int {
        return if (player2.isEmpty()) {
            player1.mapIndexed { index, card ->
                card * (player1.size - index)
            }.sum()
        } else {
            player2.mapIndexed { index, card ->
                card * (player2.size - index)
            }.sum()
        }
    }

    private fun recursivePlay(cards1: MutableList<Int>, cards2: MutableList<Int>) : Int {
        println("Playing $cards1 vs $cards2 ")
        val configs = mutableMapOf<String, MutableList<String>>()
        do {

            val config1 = cards1.joinToString { it.toString() }
            val config2 = cards2.joinToString { it.toString() }

            if (configs.isNotEmpty() && configs[config1]?.contains(config2) == true) {
                return 1
            } else {
                if (configs[config1] == null) configs[config1] = mutableListOf()
                configs[config1]!!.add(config2)
            }

            val card1 = cards1.removeAt(0)
            val card2 = cards2.removeAt(0)
            var winner = 0

            if (card1 <= cards1.size && card2 <= cards2.size) {
                winner = recursivePlay(cards1.subList(0, card1).toMutableList(), cards2.subList(0, card2).toMutableList())
            }

            when {
                winner == 1 -> cards1.addAll(listOf(card1, card2))
                winner == 2 -> cards2.addAll(listOf(card2, card1))
                card1 > card2 -> cards1.addAll(listOf(card1, card2))
                card2 > card1 -> cards2.addAll(listOf(card2, card1))
            }
        } while (cards1.isNotEmpty() && cards2.isNotEmpty())

        return if (cards2.isEmpty()) 1 else 2
    }
}
