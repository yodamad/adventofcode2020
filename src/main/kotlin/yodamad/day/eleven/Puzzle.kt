package yodamad.day.eleven

import yodamad.day.readFileIn

val EMPTY = '.'
val FREE = 'L'
val OCCUPIED = '#'

class Puzzle {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Puzzle().findSeats()
        }
    }

    var seats = mutableListOf<List<Char>>()
    val debug = false

    fun findSeats() {
        "full".readFileIn("day11").lines().forEach {
            seats.add(it.toList())
        }

        var modif: Boolean
        printMap(seats)
        var turn = 0
        do {
            modif = false
            val nextRound = mutableListOf<List<Char>>()
            seats.mapIndexed { lIdx, l ->
                l.mapIndexed { index, c -> run {
                    val newC = updateSeat(lIdx, index)
                    modif = modif || (newC != c)
                    newC
                }
                }.toList()
            }.toCollection(nextRound)
            seats = nextRound
            printMap(seats)
        } while (modif)

        val occupied = seats.map {
            it.map {
                c -> if (c == OCCUPIED) 1 else 0
            }.sum()
        }.sum()

        println("$occupied seats occupied")
    }
    
    private fun updateSeat(lineIdx: Int, idx : Int) : Char =
        when(seats[lineIdx][idx]) {
            EMPTY -> EMPTY
            FREE -> if (checkSeat(lineIdx, idx) == 0) OCCUPIED else FREE
            else -> if (checkSeat(lineIdx, idx) >= 4) FREE else OCCUPIED
        }

    private fun checkSeat(lineIdx: Int, idx : Int) : Int =
                // Line above
                seats.isSeatOccupied(lineIdx-1, idx - 1)
                    .plus(seats.isSeatOccupied(lineIdx-1, idx ))
                    .plus(seats.isSeatOccupied(lineIdx-1, idx + 1))
                // Same line
                    .plus(seats.isSeatOccupied(lineIdx, idx - 1))
                    .plus(seats.isSeatOccupied(lineIdx, idx + 1))
                // Line below
                    .plus(seats.isSeatOccupied(lineIdx + 1, idx - 1))
                    .plus(seats.isSeatOccupied(lineIdx + 1, idx))
                    .plus(seats.isSeatOccupied(lineIdx + 1, idx + 1))

    private fun printMap(elements : MutableList<List<Char>>) {
        if (debug) {
            elements.forEach {
                it.forEach { it2 -> print(it2) }
                println()
            }
            println("--------------------------")
        }
    }

}

fun Char.isOccupied() = if (this == OCCUPIED) 1 else 0

fun List<List<Char>>.isSeatOccupied(lIdx : Int, idx : Int) = this.getOrEmpty(lIdx).getOrDefault(idx).isOccupied()
fun List<List<Char>>.getOrEmpty(idx : Int) = this.getOrElse(idx) { listOf() }
fun List<Char>.getOrDefault(idx: Int) = this.getOrElse(idx) { EMPTY }
