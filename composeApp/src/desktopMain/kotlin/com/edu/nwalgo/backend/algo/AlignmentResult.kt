package com.edu.nwalgo.backend.algo


/**
 * Represents the result of a sequence alignment operation.
 *
 * @property alignedSeq1 The first aligned sequence as a string.
 * @property alignedSeq2 The second aligned sequence as a string.
 * @property scoreMatrix A 2D array representing the scoring matrix used in the alignment.
 * @property path A list of coordinate pairs representing the alignment path, useful for GUI grid path drawing.
 * @property identityPercent The percentage of identical matches between the two sequences.
 * @property gapCount The total number of gaps introduced in the alignment.
 * @property score The final alignment score.
 */
data class AlignmentResult(
    val alignedSeq1: String,
    val alignedSeq2: String,
    val scoreMatrix: Array<IntArray>,
    val path: List<Pair<Int, Int>>, // For GUI grid path drawing
    val identityPercent: Double,
    val gapCount: Int,
    val score: Int
)