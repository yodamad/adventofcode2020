package yodamad.day.day12

import yodamad.day.readFileIn
import yodamad.day.day12.Dir.*
import yodamad.day.day12.Move.*
import kotlin.math.absoluteValue

class Puzzle {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Puzzle().computeTrip()
            Puzzle().computeTripWithWaypoint()
        }
    }

    val position = Position()
    val boatPosition = Position()
    val waypoint = Position(dir = N, hValue = 10, vValue = -1)

    fun computeTrip() {
        initDirs()
        "trip".readFileIn("day12").readLines()
            .map { Action(it[0], it.subSequence(1, it.length).toString().toInt()) }
            .forEach {
                when (it.type) {
                    E.name[0], W.name[0], N.name[0], S.name[0] -> computeDir(it)
                    L.name[0] -> rotate(it, L)
                    R.name[0] -> rotate(it, R)
                    FWD -> computeDir(Action(position.dir.name[0], it.value))
                }
            }

        println("Position $position")
        println("Value = ${(position.hValue.absoluteValue + position.vValue.absoluteValue)}")
    }

    private fun computeDir(action: Action) {
        when(action.type) {
            E.name[0] -> position.hValue += action.value
            W.name[0] -> position.hValue -= action.value
            N.name[0] -> position.vValue -= action.value
            S.name[0] -> position.vValue += action.value
        }
    }

    private fun rotate(action: Action, move: Move) {
        when(action.value) {
            90 -> position.dir = if (move == R) position.dir.right[0] else position.dir.left[0]
            180 -> position.dir = if (move == R) position.dir.right[1] else position.dir.left[1]
            270 -> position.dir = if (move == R) position.dir.right[2] else position.dir.left[2]
        }
    }

    fun computeTripWithWaypoint() {
        "trip".readFileIn("day12").readLines()
            .map { Action(it[0], it.subSequence(1, it.length).toString().toInt()) }
            .forEach {
                run {
                    println("Action $it")
                    when (it.type) {
                        E.name[0], W.name[0], N.name[0], S.name[0] -> computeWaypointPosition(it)
                        L.name[0] -> rotateWaypoint(it, L)
                        R.name[0] -> rotateWaypoint(it, R)
                        FWD -> moveToWaypoint(it.value)
                    }
                }
            }
        //println("Position $boatPosition")
        println("Waypoint Value = ${(boatPosition.hValue.absoluteValue + boatPosition.vValue.absoluteValue)}")
    }

    private fun rotateWaypoint(action: Action, move: Move) {
        val backup = waypoint.hValue
        when(action.value) {
            90 -> if (move == R) {
                waypoint.hValue = waypoint.vValue * -1
                waypoint.vValue = backup
            } else {
                waypoint.hValue = waypoint.vValue
                waypoint.vValue = backup * -1
            }
            180 -> {
                waypoint.hValue = backup * -1
                waypoint.vValue = waypoint.vValue * -1
            }
            270 -> if (move == L) {
                waypoint.hValue = waypoint.vValue * -1
                waypoint.vValue = backup
            } else {
                waypoint.hValue = waypoint.vValue
                waypoint.vValue = backup * -1
            }
        }
        println("Waypoint $waypoint")
    }

    private fun computeWaypointPosition(action: Action) {
        when(action.type) {
            E.name[0] -> waypoint.hValue += action.value
            W.name[0] -> waypoint.hValue -= action.value
            N.name[0] -> waypoint.vValue -= action.value
            S.name[0] -> waypoint.vValue += action.value
        }
        println("Waypoint $waypoint")
    }

    private fun moveToWaypoint(value: Int) {
        boatPosition.hValue += (value * waypoint.hValue)
        boatPosition.vValue += (value * waypoint.vValue)
        println("Boat Position $boatPosition")
    }

    private fun initDirs() {
        S.left = listOf(E, N, W)
        S.right = listOf(W, N, E)
        E.left = listOf(N, W, S)
        E.right = listOf(S, W, N)
        N.left = listOf(W, S, E)
        N.right = listOf(E, S, W)
        W.left = listOf(S, E, N)
        W.right = listOf(N, E, S)

    }
}

data class Position(var dir: Dir = E, var hValue: Int = 0, var vValue: Int = 0)

data class Action(val type : Char, val value : Int)

enum class Dir(var left: List<Dir> = listOf(), var right: List<Dir> = listOf()){
    S, E, N, W
}

enum class Move{L, R}

const val FWD = 'F'
