package yodamad.day.day22

import yodamad.day.readFileIn

class Puzzle {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val puzzle = Puzzle()
            puzzle.distribution()
            println(puzzle.player1)
            println(puzzle.player2)
            puzzle.play()
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
}
