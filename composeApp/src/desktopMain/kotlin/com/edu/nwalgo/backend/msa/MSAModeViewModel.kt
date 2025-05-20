package com.edu.nwalgo.backend.msa

import androidx.lifecycle.ViewModel
import com.edu.nwalgo.backend.algo.AlignmentResult
import com.edu.nwalgo.backend.algo.needlemanWunsch
import com.edu.nwalgo.backend.multipleseqaligner.MultipleAlignmentResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MSAModeViewModel: ViewModel() {

    private val _alignmentResult = MutableStateFlow<MultipleAlignmentResult?>(null)
    val alignmentResult: StateFlow<MultipleAlignmentResult?> = _alignmentResult

    private val match = 1
    private val mismatch = -1
    private val gap = -1

    fun alignSequences(sequences: List<String>) {
        if (sequences.size < 2) return

        val result = performProgressiveAlignment(sequences)
        _alignmentResult.value = result
    }

    private fun performProgressiveAlignment(sequences: List<String>): MultipleAlignmentResult {
        // 1. Compute pairwise scores
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

        // 2. Find center sequence
        val totalScores = pairwiseScores.map { it.sum() }
        val centerIndex = totalScores.indices.minByOrNull { totalScores[it] }!!

        var currentAlignments = mutableListOf(sequences[centerIndex])

        // 3. Align all others progressively
        for (i in sequences.indices) {
            if (i == centerIndex) continue
            val result = needlemanWunsch(currentAlignments[0], sequences[i], match, mismatch, gap)
            currentAlignments = mergeAligned(currentAlignments, result.alignedSeq1)
            currentAlignments.add(result.alignedSeq2)
        }

        // 4. Evaluate result
        val gaps = currentAlignments.sumOf { it.count { ch -> ch == '-' } }
        val matches = currentAlignments[0].indices.count { idx ->
            val column = currentAlignments.map { it[idx] }
            column.toSet().size == 1 && column[0] != '-'
        }
        val identity = matches.toDouble() / currentAlignments[0].length * 100
        val score = calculateTotalScore(currentAlignments, match, mismatch, gap)

        return MultipleAlignmentResult(
            alignedSequences = currentAlignments,
            identity = identity,
            gapCount = gaps,
            score = score
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
