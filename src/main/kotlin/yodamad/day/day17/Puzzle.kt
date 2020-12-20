package yodamad.day.day17

import yodamad.day.readFileIn

class Puzzle {

    companion object {
        const val ACTIVE = '#'
        const val INACTIVE = '.'
        @JvmStatic
        fun main(args: Array<String>) {
            Puzzle().states(false)
            Puzzle().states(true)
        }
    }

    private var cube = mutableMapOf<Point, Char>()
    private var newCube = mutableMapOf<Point, Char>()

    private fun states(part2: Boolean) {
        if (part2) initialState4D() else initialState()
        var currentCycle = 1
        do {
            println("Cycle = $currentCycle")
            if (part2) updateLayers4D() else updateLayers()
            if (part2) nextStates4D() else nextStates()
            currentCycle++
            val result = cube.values.filter { it == ACTIVE }.count()
            println("result is $result")
            println("=========")
        } while (currentCycle < 7)
    }

    private fun initialState() =
        "full".readFileIn("day17").readLines()
            .mapIndexed { idx, l ->
                l.mapIndexed { index, c ->
                    if (cube.isNullOrEmpty()) cube = mutableMapOf()
                    cube[Point(idx, index, 0)] = c
                }
            }

    private fun initialState4D() =
        "full".readFileIn("day17").readLines()
            .mapIndexed { idx, l ->
                l.mapIndexed { index, c ->
                    if (cube.isNullOrEmpty()) cube = mutableMapOf()
                    cube[Point(idx, index, 0, 0)] = c
                }
            }

    private fun updateLayers() {
        newCube.putAll(cube)
        cube.keys.forEach { point ->
            neighbors(point).forEach { neighbor ->
                newCube.putIfAbsent(neighbor, INACTIVE)
            }
        }
    }

    private fun updateLayers4D() {
        newCube.putAll(cube)
        cube.keys.forEach { point ->
            neighbors4D(point).forEach { neighbor ->
                newCube.putIfAbsent(neighbor, INACTIVE)
            }
        }
    }

    private fun nextStates() {
        newCube.forEach { pt ->
            val active = neighbors(pt.key)
                .map { newCube.getOrDefault(it, INACTIVE) }
                .filter { it == ACTIVE }
                .count()
            updatePoint(pt.key, active)
        }
        newCube = mutableMapOf()
    }

    private fun nextStates4D() {
        newCube.forEach { pt ->
            val active = neighbors4D(pt.key)
                .map { newCube.getOrDefault(it, INACTIVE) }
                .filter { it == ACTIVE }
                .count()
            updatePoint(pt.key, active)
        }
        newCube = mutableMapOf()
    }

    private fun updatePoint(pt: Point, activeNeighbors: Int) {
        if (activeNeighbors == 3 && newCube[pt] == INACTIVE) {
            cube[pt] = ACTIVE
        } else if (activeNeighbors in setOf(2, 3) && newCube[pt] == ACTIVE) {
            cube[pt] = ACTIVE
        } else {
            cube[pt] = INACTIVE
        }
    }


    private fun neighbors(point: Point): List<Point> =
        (point.x - 1 .. point.x + 1).flatMap { myX ->
            (point.y - 1 .. point.y + 1).flatMap { myY ->
                (point.z - 1 .. point.z + 1).mapNotNull { myZ ->
                    Point(myX, myY, myZ).takeUnless { it == point }
                }
            }
        }.toList()

    private fun neighbors4D(point: Point): List<Point> =
        (point.x - 1 .. point.x + 1).flatMap { myX ->
            (point.y - 1 .. point.y + 1).flatMap { myY ->
                (point.z - 1 .. point.z + 1).flatMap { myZ ->
                    (point.t - 1 .. point.t + 1).mapNotNull { myT ->
                        Point(myX, myY, myZ, myT).takeUnless { it == point }
                    }
                }
            }
        }.toList()

    private fun printMap(map: MutableMap<Point, Char>) {
        var currentY = Int.MIN_VALUE
        var currentZ = Int.MIN_VALUE

        map.keys
            .sortedWith(compareBy({it.z}, {it.y }))
            .forEachIndexed { _, point ->
                if (currentY != point.y) {
                    println()
                    currentY = point.y
                }
                if (currentZ != point.z) {
                    println()
                    currentZ = point.z
                }
                print("${map[point]}$point")
            }
        println()
    }
}

data class Point(var x: Int, var y: Int, var z: Int, var t: Int = Int.MIN_VALUE) {
    override fun equals(other: Any?): Boolean = other is Point && other.x == x && other.y == y && other.z == z && other.t == t
    override fun toString(): String = ""//"($x,$y,$z)"
    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        result = 31 * result + t
        return result
    }
}
