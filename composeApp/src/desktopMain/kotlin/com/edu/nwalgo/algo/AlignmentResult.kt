package com.edu.nwalgo.algo

data class AlignmentResult(
    val alignedSeq1: String,
    val alignedSeq2: String,
    val scoreMatrix: Array<IntArray>,
    val path: List<Pair<Int, Int>>, // For GUI grid path drawing
    val identityPercent: Double,
    val gapCount: Int,
    val score: Int
)
