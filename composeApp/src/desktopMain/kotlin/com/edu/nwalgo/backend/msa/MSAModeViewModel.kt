package com.edu.nwalgo.backend.msa

import DocumentManager
import androidx.lifecycle.ViewModel
import com.edu.nwalgo.backend.algo.AlignmentResult
import com.edu.nwalgo.backend.algo.needlemanWunsch
import com.edu.nwalgo.backend.fastaparser.FastaEntry
import com.edu.nwalgo.backend.multipleseqaligner.MultipleAlignmentResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class MSAModeViewModel: ViewModel() {

    private val DocumentManager = DocumentManager()

    private val _alignmentResult = MutableStateFlow<MultipleAlignmentResult?>(null)
    val alignmentResult: StateFlow<MultipleAlignmentResult?> = _alignmentResult

    private var match = 1
    private var  mismatch = -1
    private var gap = -1

    fun updateMatchScore(newMatch: Int) {
        match = newMatch
    }
    fun updateMismatchScore(newMismatch: Int) {
        mismatch = newMismatch
    }
    fun updateGapScore(newGap: Int) {
        gap = newGap
    }

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

        for (i in sequences.indices) {
            if (i == centerIndex) continue
            val result = needlemanWunsch(currentAlignments[0], sequences[i], match, mismatch, gap)
            currentAlignments = mergeAligned(currentAlignments, result.alignedSeq1)
            currentAlignments.add(result.alignedSeq2)
        }

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

    fun loadFirstFastaSequence(file: File): String {
        val content = file.readText()
        val entries = parseFasta(content)
        if (entries.isEmpty()) throw Exception("No sequences found in file.")
        return entries[0].sequence
    }

    fun parseFasta(content: String): List<FastaEntry> {
        return content.trim().split(">").filter { it.isNotBlank() }.map { entry ->
            val lines = entry.lines().filter { it.isNotBlank() }
            val header = lines.first().trim()
            val sequence = lines.drop(1).joinToString("").replace("\\s".toRegex(), "").uppercase()
            if (sequence.isBlank()) throw IllegalArgumentException("Empty sequence for header: $header")
            FastaEntry(header, sequence)
        }
    }

    fun exportAlignmentReportToPDF() {
        alignmentResult.value?.let { result ->
            DocumentManager.generateMSAReport(result)
        }
    }
    fun exportAlignmentImageToSVG() {
        alignmentResult.value?.let { result ->
            DocumentManager.generateAlignmentSVG(result.alignedSequences, result.identity, result.gapCount, result.score)
        }
    }

    fun showVisualization() {
        alignmentResult.value?.let { result ->
            DocumentManager.showMSAVisualization(result.alignedSequences, result.identity)
        }
    }















}
