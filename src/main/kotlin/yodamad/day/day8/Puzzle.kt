package yodamad.day.day8

import yodamad.day.readFileIn

class Puzzle {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Puzzle().runProgram()
        }
    }

    var accumulator = 0
    val commands = mutableMapOf<Int, Command>()
    val alreadyVisited = mutableListOf<Int>()
    val potentialIndexes = mutableListOf<Int>()
    var currentPotential = -1

    fun runProgram() {
        val lines = "instructions".readFileIn("day8").readLines()

        for (index in 0 until lines.count()) {
            val command = Command(lines[index].split(" ")[0],
                lines[index].split(" ")[1].toInt())
            commands[index] = command
            if (command.ope in listOf(OP.nop.name, OP.jmp.name)) potentialIndexes.add(index)
        }

        while (++currentPotential < potentialIndexes.size && !processCommand(0)) {
            accumulator = 0
            alreadyVisited.clear()
        }
        println("accu = $accumulator")
    }

    private tailrec fun processCommand(index: Int, alternative: String = "") : Boolean {

        // We've reached out
        if (commands.size == index) return true

        var command = alternative
        if (command.isEmpty()) command = commands[index]?.ope!!

        if (alreadyVisited.contains(index)) return false
        alreadyVisited.add(index)

        //println("Run {$index} $command ${commands[index]?.value!!}")

        when(command) {

            OP.nop.name -> {
                return if (potentialIndexes.get(currentPotential) == index && commands[index]?.value!! != 0) {
                    //println("Trying jmp rather than nop")
                    processCommand(index + commands[index]?.value!!)
                } else {
                    processCommand(index+1)
                }
            }
            OP.jmp.name -> {
                return if (potentialIndexes.get(currentPotential) == index) {
                    //println("Trying nop rather than jmp")
                    processCommand(index+1)
                } else {
                    processCommand(index + commands[index]?.value!!)
                }
            }
            OP.acc.name -> {
                accumulator += commands[index]?.value!!
                return processCommand(index+1)
            }
        }
        return processCommand(index+1)
    }
}

enum class OP { nop, jmp, acc }

data class Command(val ope: String, val value: Int)
