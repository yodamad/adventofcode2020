import yodamad.day.day1.INPUT
import java.util.stream.Collectors.toList

data class Tuple(val val1 : Int, val val2: Int)
data class Triple(val val1 : Int, val val2: Int, val val3: Int)

class Puzzle {

    fun findTuple2020(): Int {

        val min = INPUT.minOrNull()
        val max = INPUT.maxOrNull()

        val candidates = INPUT.stream()
            .filter{
                    e -> e + max!! > 2020
            }.filter{
                    e -> e + min!! < 2021
            }.collect(toList())

        var tuple : Tuple? = null
        // Ugly
        candidates.forEach { it1 ->
            candidates.forEach {
                it2 -> if (it1 + it2 == 2020) {
                    tuple = Tuple(it1, it2)
                }
            }
        }

        return tuple!!.val1 * tuple!!.val2
    }

    fun findTriple2020(): Int {

        var triple : Triple? = null
        // Very ugly
        INPUT.forEach { it1 ->
            INPUT.forEach { it2 ->
                INPUT.forEach {
                    it3 -> if (it1 + it2 + it3 == 2020) {
                        triple = Triple(it1, it2, it3)
                    }
                }
            }
        }

        return triple!!.val1 * triple!!.val2  * triple!!.val3
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println(Puzzle().findTuple2020())
            println(Puzzle().findTriple2020())
        }
    }
}
