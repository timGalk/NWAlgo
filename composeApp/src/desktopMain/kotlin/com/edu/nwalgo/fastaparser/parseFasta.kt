package com.edu.nwalgo.fastaparser

fun parseFasta(content: String): List<FastaEntry> {
    return content.trim().split(">").filter { it.isNotBlank() }.map { entry ->
        val lines = entry.lines()
        val header = lines.first().trim()
        val sequence = lines.drop(1).joinToString("").replace("\\s".toRegex(), "").uppercase()
        FastaEntry(header, sequence)
    }
}