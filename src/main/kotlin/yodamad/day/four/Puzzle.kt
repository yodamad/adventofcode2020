package yodamad.day.four

import java.io.File

class Puzzle {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Puzzle().findValidPassports()
        }
    }

    fun findValidPassports() {
        val inputStream = File("./src/main/resources/day4/passports").inputStream()
        val lines = inputStream.bufferedReader().lines()

        val passports  = mutableListOf<Map<String, String>>()
        var currentPassport = mutableMapOf<String, String>()

        lines.forEach {
            l -> run {
                if (l.isEmpty()) {
                    // End of passport, initialize a new one
                    passports.add(currentPassport)
                    currentPassport = mutableMapOf<String, String>()
                } else {
                    // Retrieve only key part
                    l.split(" ").forEach {
                        currentPassport[it.split(":")[0]] = it.split(":")[1]
                    }
                }
            }
        }

        println("${passports.size} passports")

        val valids = passports
                // Filter only the keys (part 1)
            .filter{
                element -> element.keys.containsAll(listOfKeys())
            }

        val ok = passports
            .filter{
                    element -> element.keys.containsAll(listOfKeys())
            }
            .filter { it.isValidPassport() }

        println(valids)
        println("${valids.size} valid passports")
        println("${ok.size} really valid passports")
    }
}

enum class KEYS(val optional: Boolean = false, val validation: (String) -> Boolean) {
    BYR(validation = { validYear (1920, 2002, it) }),
    IYR(validation = { validYear (2010, 2020, it) }),
    EYR(validation = { validYear (2020, 2030, it) }),
    HGT(validation = { internationalValidHeight(it) }),
    HCL(validation = { validHairColor(it) }),
    ECL(validation = { validEyeColor(it) }),
    PID(validation = { validPassportID(it)}),
    CID(optional = true, validation = { true })
}

fun listOfKeys() = KEYS.values()
    .filter { v -> !v.optional }
    .map { k -> k.name.toLowerCase() }.toCollection(mutableListOf())

fun validYear(min: Int, max: Int, value: String) = value.toInt() in min..max

fun internationalValidHeight(value: String) = validHeight(value, 150, 193, "cm") ||
        validHeight(value, 59, 76, "in")

fun validHeight(value: String, min: Int, max: Int, unity: String) =
    value.endsWith(unity) && value.replace(unity, "").toInt() in min..max

fun validHairColor(value: String) = Regex("#[0-9a-z]{6}").matches(value)

fun validEyeColor(value: String) = listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(value)

fun validPassportID(value: String) = Regex("[0-9]{9}").matches(value)

fun Map<out String, String>.isValidPassport(): Boolean = this
    .filter {
        (key, value) -> !debugValidation(key, value)
    }.isEmpty()

fun debugValidation(key : String, value : String) : Boolean {
    val result = KEYS.valueOf(key.toUpperCase()).validation(value)
    println("$key is valid ? $result (value = $value)")
    return result
}
