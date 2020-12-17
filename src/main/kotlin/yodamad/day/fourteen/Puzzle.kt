package yodamad.day.fourteen

import yodamad.day.readFileIn

class Puzzle {

    companion object {
        const val DEFAULT_MASK = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
        @JvmStatic
        fun main(args: Array<String>) {
            //Puzzle().computeMemory()
            Puzzle().computeFloatingMemory()
        }
    }

    private val masks = mutableMapOf<String, MutableList<MemorySlot>>()
    private val memory = mutableMapOf<Int, String>()
    private val floatingMemory = mutableMapOf<Long, String>()

    private fun initMask() = "memory".readFileIn("day14").lines()
        .map { l -> run {
            if (l.startsWith("mask")) {
                addMask(l)
            } else { addMemory(l) }
        }
        }.count()

    private fun computeMemorySum() = memory.values.map { it.toLong(2) }.sum()

    fun computeMemory() {
        initMask()
        masks.map { m ->
            m.value.map { ms ->
                val binString = ms.value.toBinaryString().toCharArray()
                m.key.onEachIndexed { index, c ->
                    when (c) {
                        '1' -> binString[index] = '1'
                        '0' -> binString[index] = '0'
                    }
                    memory[ms.offset] = String(binString)
                }
            }
        }
        println("Memory status is $memory")
        println("Memory value = ${computeMemorySum()}")
    }

    fun computeFloatingMemory() {
        initMask()
        masks.forEach { m ->
            m.value.forEach { ms ->
                val offsets = mutableListOf(ms.offset.toBinaryString().toCharArray())
                m.key.forEachIndexed { idx, char ->
                    when (char) {
                        '1' -> {
                            offsets.forEach { it[idx] = '1' }
                        }
                        'X' -> {
                            offsets.forEach { it[idx] = '1' }
                            offsets.addAll(
                                offsets.map {
                                    it.copyOf().apply { this[idx] = '0' }
                                }
                            )
                        }
                    }
                }

                offsets.map { it.joinToString("").toLong(2) }.map {
                    floatingMemory[it] = ms.value.toBinaryString()
                }
            }
        }
        // println("Memory status is $floatingMemory")
        println("Memory value = ${computeFloatingMemorySum()}")
    }

    private fun computeFloatingMemorySum() = floatingMemory.values.map { it.toLong(2) }.sum()

    private fun addMask(line: String) {
        masks[line.split("=")[1].trimStart()] = mutableListOf()
    }

    private fun addMemory(line: String) {
        val parts = line.split("=")
        masks.entries.last().value.add(MemorySlot(parts[0].trim().offset()!!, parts[1].trimStart().toInt()))
    }

    private fun computeSwitch(input: Int, floatingOffset: Int, offsets: MutableList<String>): MutableList<String> {
        val newOffsets = mutableListOf<String>()
        if (offsets.isEmpty()) {
            val binInput = input.toBinaryString().toCharArray()
            binInput[floatingOffset] = '1'
            newOffsets.add(String(binInput))
        } else {
            offsets.forEach {
                val binInput = it.toCharArray()
                binInput[floatingOffset] = '1'
                newOffsets.add(String(binInput))
            }
            return newOffsets
        }
        return newOffsets
    }

    private fun computeOffsets(input: Int, floatingOffset: Int, offsets: MutableList<String>): MutableList<String> {
        if (offsets.isEmpty()) {
            //println("Index = $floatingOffset, ${input.toBinaryString()}")
            var binInput = input.toBinaryString().toCharArray()
            binInput[floatingOffset] = '0'
            offsets.add(String(binInput))
            binInput = input.toBinaryString().toCharArray()
            binInput[floatingOffset] = '1'
            offsets.add(String(binInput))
            return offsets
        } else {
            val newOffsets = mutableListOf<String>()
            offsets.forEach {
                var binInput = it.toCharArray()
                binInput[floatingOffset] = '0'
                newOffsets.add(String(binInput))
                binInput = it.toCharArray()
                binInput[floatingOffset] = '1'
                newOffsets.add(String(binInput))
            }
            return newOffsets
        }
    }
}

fun Int.toBinaryString() = Integer.toBinaryString(this).padStart(36, '0')

fun String.offset() = Regex("mem\\[(\\d+)\\]").find(this)?.groupValues?.get(1)?.toInt()

data class MemorySlot(val offset: Int, val value: Int)
