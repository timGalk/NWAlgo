package com.edu.nwalgo.backend.algo
/**
 * Implements the Needleman-Wunsch algorithm for sequence alignment.
 *
 * @param seq1 The first sequence to align.
 * @param seq2 The second sequence to align.
 * @param match The score for a match between characters in the sequences.
 * @param mismatch The penalty for a mismatch between characters in the sequences.
 * @param gap The penalty for introducing a gap in the alignment.
 * @return An `AlignmentResult` object containing the aligned sequences, score matrix, alignment path,
 *         identity percentage, gap count, and final alignment score.
 */
fun needlemanWunsch(
    seq1: String,
    seq2: String,
    match: Int,
    mismatch: Int,
    gap: Int
): AlignmentResult {
    val m = seq1.length
    val n = seq2.length

    val matrix = Array(m + 1) { IntArray(n + 1) }
    val path = mutableListOf<Pair<Int, Int>>()

    // Initialize the first row and column
    for (i in 0..m) matrix[i][0] = i * gap
    for (j in 0..n) matrix[0][j] = j * gap

    // Fill the score matrix
    for (i in 1..m) {
        for (j in 1..n) {
            val scoreDiagonal = matrix[i - 1][j - 1] + if (seq1[i - 1] == seq2[j - 1]) match else mismatch
            val scoreUp = matrix[i - 1][j] + gap
            val scoreLeft = matrix[i][j - 1] + gap
            matrix[i][j] = maxOf(scoreDiagonal, scoreUp, scoreLeft)
        }
    }

    // Traceback
    var i = m
    var j = n
    val aligned1 = StringBuilder()
    val aligned2 = StringBuilder()
    var matches = 0
    var gaps = 0

    while (i > 0 || j > 0) {
        path.add(Pair(i, j))
        when {
            i > 0 && j > 0 && matrix[i][j] == matrix[i - 1][j - 1] + if (seq1[i - 1] == seq2[j - 1]) match else mismatch -> {
                aligned1.append(seq1[i - 1])
                aligned2.append(seq2[j - 1])
                if (seq1[i - 1] == seq2[j - 1]) matches++
                i--; j--
            }
            i > 0 && matrix[i][j] == matrix[i - 1][j] + gap -> {
                aligned1.append(seq1[i - 1])
                aligned2.append('-')
                gaps++
                i--
            }
            else -> {
                aligned1.append('-')
                aligned2.append(seq2[j - 1])
                gaps++
                j--
            }
        }
    }

    path.add(Pair(0, 0))
    path.reverse()

    val alignedStr1 = aligned1.reverse().toString()
    val alignedStr2 = aligned2.reverse().toString()
    val identity = matches.toDouble() / alignedStr1.length * 100
    val finalScore = matrix[m][n]

    return AlignmentResult(
        alignedSeq1 = alignedStr1,
        alignedSeq2 = alignedStr2,
        scoreMatrix = matrix,
        path = path,
        identityPercent = identity,
        gapCount = gaps,
        score = finalScore
    )
}