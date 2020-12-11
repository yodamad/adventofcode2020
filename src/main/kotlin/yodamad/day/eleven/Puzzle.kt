package yodamad.day.eleven

import yodamad.day.eleven.DIR.*
import yodamad.day.readFileIn

val EMPTY = '.'
val FREE = 'L'
val OCCUPIED = '#'

class Puzzle {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            //Puzzle().findSeats()
            Puzzle().findFarAwaySeats()
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

    fun findFarAwaySeats() {
        "full".readFileIn("day11").lines().forEach {
            seats.add(it.toList())
        }

        var modif: Boolean
        printMap(seats)
        do {
            modif = false
            val nextRound = mutableListOf<List<Char>>()
            seats.mapIndexed { lIdx, l ->
                l.mapIndexed { index, c -> run {
                    val newC = updateFarAwaySeat(lIdx, index)
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

    private fun updateFarAwaySeat(lineIdx: Int, idx : Int) : Char =
        when(seats[lineIdx][idx]) {
            EMPTY -> EMPTY
            FREE -> if (checkSeats(lineIdx, idx) == 0) OCCUPIED else FREE
            else -> if (checkSeats(lineIdx, idx) >= 5) FREE else OCCUPIED
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

    private fun checkSeats(lineIdx: Int, idx : Int) : Int =
        DIR.values().toList().map { checkWithDir(lineIdx, idx, it).isOccupied() }.sum()

    private fun checkWithDir(lineIdx: Int, idx: Int, dir: DIR) : Char =
        when(dir) {
            UP -> {
                var result = EMPTY
                var round = lineIdx - 1
                while (round >= 0 && result == EMPTY) {
                    result = seats.getOrEmpty(round).getOrDefault(idx)
                    round--
                }
                printSeat(lineIdx, idx, result, UP)
                result
            }
            DOWN -> {
                var result = EMPTY
                var round = lineIdx + 1
                while (round < seats.size && result == EMPTY) {
                    result = seats.getOrEmpty(round).getOrDefault(idx)
                    round++
                }
                printSeat(lineIdx, idx, result, DOWN)
                result
            }
            LEFT -> {
                var result = EMPTY
                var round = idx - 1
                while (round >= 0 && result == EMPTY) {
                    result = seats.getOrEmpty(lineIdx).getOrDefault(round)
                    round--
                }
                printSeat(lineIdx, idx, result, LEFT)
                result
            }
            RIGHT -> {
                var result = EMPTY
                var round = idx + 1
                while (round < seats[lineIdx].size && result == EMPTY) {
                    result = seats.getOrEmpty(lineIdx).getOrDefault(round)
                    round++
                }
                printSeat(lineIdx, idx, result, RIGHT)
                result
            }
            UP_RIGHT -> {
                var result = EMPTY
                var roundX = idx + 1
                var roundY = lineIdx - 1
                while (roundY >= 0 && roundX < seats[lineIdx].size && result == EMPTY) {
                    result = seats.getOrEmpty(roundY).getOrDefault(roundX)
                    roundX++
                    roundY--
                }
                printSeat(lineIdx, idx, result, UP_RIGHT)
                result
            }
            UP_LEFT -> {
                var result = EMPTY
                var roundX = idx - 1
                var roundY = lineIdx - 1
                while (roundY >= 0 && roundX >= 0 && result == EMPTY) {
                    result = seats.getOrEmpty(roundY).getOrDefault(roundX)
                    roundX--
                    roundY--
                }
                printSeat(lineIdx, idx, result, UP_LEFT)
                result
            }
            DOWN_RIGHT -> {
                var result = EMPTY
                var roundX = idx + 1
                var roundY = lineIdx + 1
                while (roundY < seats.size && roundX < seats[lineIdx].size && result == EMPTY) {
                    result = seats.getOrEmpty(roundY).getOrDefault(roundX)
                    roundX++
                    roundY++
                }
                printSeat(lineIdx, idx, result, DOWN_RIGHT)
                result
            }
            DOWN_LEFT -> {
                var result = EMPTY
                var roundX = idx - 1
                var roundY = lineIdx + 1
                while (roundY < seats.size && roundX >= 0 && result == EMPTY) {
                    result = seats.getOrEmpty(roundY).getOrDefault(roundX)
                    roundX--
                    roundY++
                }
                printSeat(lineIdx, idx, result, DOWN_LEFT)
                result
            }
        }

    private fun printSeat(lineIdx: Int, index: Int, seat: Char, dir : DIR) {
        if (debug && lineIdx == seats.size - 3 && index == seats[0].size - 1) println("Seat is $seat in $dir")
    }

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

enum class DIR { UP, DOWN, LEFT, RIGHT, UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT}
