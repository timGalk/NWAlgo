package com.edu.nwalgo.backend.multipleseqaligner


data class MultipleAlignmentResult(
    val alignedSequences: List<String>,
    val identity: Double,
    val gapCount: Int,
    val score: Int
)
