package com.edu.nwalgo.backend.algo

import com.edu.nwalgo.backend.multipleseqaligner.MultipleAlignmentResult

class ProgressiveAligner(
    private val match: Int = 1,
    private val mismatch: Int = -1,
    private val gap: Int = -1
) {

    fun align(sequences: List<String>): MultipleAlignmentResult {
        require(sequences.size >= 2) { "Need at least 2 sequences" }

        // 1. Вычислить матрицу попарных выравниваний
        val pairwiseScores = Array(sequences.size) { IntArray(sequences.size) }
        val pairwiseAlignments = Array(sequences.size) { arrayOfNulls<AlignmentResult>(sequences.size) }

        for (i in sequences.indices) {
            for (j in i + 1 until sequences.size) {
                val result = needlemanWunsch(sequences[i], sequences[j], match, mismatch, gap)
                pairwiseScores[i][j] = result.score
                pairwiseScores[j][i] = result.score
                pairwiseAlignments[i][j] = result
                pairwiseAlignments[j][i] = result
            }
        }

        // 2. Найти центральную последовательность
        val totalScores = pairwiseScores.map { it.sum() }
        val centerIndex = totalScores.indices.minByOrNull { totalScores[it] }!!

        var currentAlignments = mutableListOf(sequences[centerIndex])

        // 3. Поочередно выравниваем другие последовательности к центральной
        for (i in sequences.indices) {
            if (i == centerIndex) continue
            val result = needlemanWunsch(currentAlignments[0], sequences[i], match, mismatch, gap)

            // Обновляем все текущие выравнивания (вставка gap'ов)
            currentAlignments = mergeAligned(currentAlignments, result.alignedSeq1)
            currentAlignments.add(result.alignedSeq2)
        }

        val gaps = currentAlignments.sumOf { it.count { ch -> ch == '-' } }
        val matches = currentAlignments[0].indices.count { idx ->
            val column = currentAlignments.map { it[idx] }
            column.toSet().size == 1 && column[0] != '-'
        }
        val identity = matches.toDouble() / currentAlignments[0].length * 100

        return MultipleAlignmentResult(
            alignedSequences = currentAlignments,
            identity = identity,
            gapCount = gaps,
            score = calculateTotalScore(
                alignedSequences = currentAlignments,
                match = match,
                mismatch = mismatch,
                gap = gap
            )
        )
    }

    private fun mergeAligned(existing: List<String>, newAlignedCenter: String): MutableList<String> {
        val result = mutableListOf<String>()

        for (seq in existing) {
            val updated = StringBuilder()
            var i = 0
            for (j in newAlignedCenter.indices) {
                if (newAlignedCenter[j] == '-') {
                    updated.append('-')
                } else {
                    updated.append(seq[i])
                    i++
                }
            }
            result.add(updated.toString())
        }

        return result
    }
    private fun calculateTotalScore(
        alignedSequences: List<String>,
        match: Int,
        mismatch: Int,
        gap: Int
    ): Int {
        val n = alignedSequences.size
        val length = alignedSequences[0].length
        var totalScore = 0

        for (col in 0 until length) {
            for (i in 0 until n) {
                for (j in i + 1 until n) {
                    val a = alignedSequences[i][col]
                    val b = alignedSequences[j][col]

                    totalScore += when {
                        a == '-' || b == '-' -> gap
                        a == b -> match
                        else -> mismatch
                    }
                }
            }
        }

        return totalScore
    }

}
