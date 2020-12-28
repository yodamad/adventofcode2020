package yodamad.day.day25

class Puzzle {
    companion object {
        const val DOOR = 10441485L
        const val CARD = 1004920L

        const val SAMPLE_CARD = 5764801L
        const val SAMPLE_DOOR = 17807724L

        const val DEBUG = false

        @JvmStatic
        fun main(args: Array<String>) {
            Puzzle().checkLoop()
            Puzzle().reverse()
        }
    }

    private fun checkLoop() {
        val subject = 7L
        var result = 1L
        repeat(8) {
            result *= subject
            result = result.rem(20201227)
        }
        println("Result = $result (${result == SAMPLE_CARD})")

        result = 1L
        repeat(11) {
            result *= subject
            result = result.rem(20201227)
        }
        println("Result = $result (${result == SAMPLE_DOOR})")
    }

    private fun reverse() {
        var subject = 7L
        var result = 1L
        var loop = 0

        while (result != CARD) {
            loop++
            result *= subject
            result = result.rem(20201227)
        }
        val cardLoop = loop
        println("Nb loops for card = $loop")

        loop = 0
        result = 1L

        while (result != DOOR) {
            loop++
            result *= subject
            result = result.rem(20201227)
        }
        val doorLoop = loop
        println("Nb loops for door = $loop")

        result = 1
        subject = CARD
        repeat(doorLoop) {
            result *= subject
            result = result.rem(20201227)
        }
        println("Encryption key is $result")

        result = 1
        subject = DOOR
        repeat(cardLoop) {
            result *= subject
            result = result.rem(20201227)
        }
        println("Encryption key is $result")
    }
}
