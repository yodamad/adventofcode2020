package yodamad.day

import java.io.BufferedReader
import java.io.File

fun String.readFileIn(dir: String) : BufferedReader =
    File("./src/main/resources/$dir/$this").inputStream().bufferedReader()
