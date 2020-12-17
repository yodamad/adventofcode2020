package yodamad.day.day15

class Puzzle {

    companion object {

        val samples = listOf("0,3,6", "1,3,2", "2,1,3", "1,2,3", "2,3,1", "3,2,1", "3,1,2")
        const val PUZZLE = "18,11,9,0,5,1"

        @JvmStatic
        fun main(args: Array<String>) {
            Puzzle().find2020()
        }
    }

    fun find2020() {
        samples.forEach { compute2020(it) }
        compute2020(PUZZLE)
    }

    private fun compute2020(input: String) {
        val ages = mutableMapOf<Int, Pair<Int, Int>>()
        var spoken = 0
        input.split(",").mapIndexed { index, s ->
            ages[s.toInt()] = Pair(index + 1, -1)
            spoken = s.toInt()
        }

        for (idx in ages.size+1 .. 30000000) {
            when {
                ages[spoken] == null -> {
                    ages[spoken] = Pair(idx, -1)
                    spoken = 0
                }
                ages[spoken]!!.second == -1 -> {
                    spoken = 0
                    if (ages[spoken] == null) {
                        ages[spoken] = Pair(idx, -1)
                    } else {
                        ages[spoken] = Pair(idx, ages[spoken]!!.first)
                    }
                }
                else -> {
                    spoken = (idx-1) - ages[spoken]!!.second
                    if (ages[spoken] == null) {
                        ages[spoken] = Pair(idx, -1)
                    } else {
                        ages[spoken] = Pair(idx, ages[spoken]!!.first)
                    }
                }
            }
        }
        println("$input as result $spoken")
    }
}
