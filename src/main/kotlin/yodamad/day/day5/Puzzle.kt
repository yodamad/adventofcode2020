package yodamad.day.day5

import java.io.File
import java.util.*
import java.util.stream.IntStream

class Puzzle {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println(Puzzle().findBetterSeat())

            println(Puzzle().findMySeat())
        }
    }

    fun findBetterSeat() : OptionalInt {
        return computeAllSeats()!!.max()
    }

    fun findMySeat(): MutableList<Int> {
        val seats = computeAllSeats()!!.sorted().toArray()
        val available = mutableListOf<Int>()

        for (index in 1 until seats.size - 2) {
            println(seats[index])
            if (seats[index+1] != seats[index]+1) {
                available.add(seats[index]+1)
            }
        }

        return available
    }

    fun computeAllSeats(): IntStream? {
        val inputStream = File("./src/main/resources/day5/boarding_passes").inputStream()
        return inputStream.bufferedReader().lines().map { computeSeat(it) }.mapToInt{ it }
    }

    fun computeSeat(seatNumber: String): Int {

        var row = 0
        var column = 0

        var minRow = 0
        var maxRow = 127

        var minColumn = 0
        var maxColumn = 7

        for (element in seatNumber.toList()) {
            when (element) {
                'F' -> {
                    maxRow = ((maxRow + minRow + 1) / 2) - 1
                    row = maxRow }
                'B' -> {
                    minRow = (maxRow + minRow + 1) / 2
                    row = minRow
                }
                'R' -> {
                    minColumn = (minColumn + maxColumn + 1) / 2
                    column = minColumn
                }
                'L' -> {
                    maxColumn = ((minColumn + maxColumn + 1) / 2) - 1
                    column = maxColumn
                }
            }
        }

        return row * 8 + column
    }
}
