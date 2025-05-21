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

/**
 * ViewModel class responsible for managing the state and business logic
 * of the Multiple Sequence Alignment (MSA) operation.
 * This class provides methods for updating scoring parameters, performing
 * sequence alignment, and generating reports or visualizations of the alignment results.
 */
class MSAModeViewModel: ViewModel() {

    /**
     * Manages and provides functionalities for handling document-related
     * operations within MSAModeViewModel. It serves as the primary entry
     * point for interacting with and managing documents.
     */
    private val DocumentManager = DocumentManager()

    /**
     * A private MutableStateFlow used to store the result of multiple sequence alignment operations.
     * Holds an instance of `MultipleAlignmentResult` or `null` if no alignment has been performed.
     *
     * This state flow is updated after the `performProgressiveAlignment` method processes a set
     * of sequences and is used internally for data synchronization and state management.
     */
    private val _alignmentResult = MutableStateFlow<MultipleAlignmentResult?>(null)
    /**
     * A [StateFlow] that holds the current result of a multiple sequence alignment process.
     * The emitted value is an instance of [MultipleAlignmentResult] or null if no alignment
     * process has been performed or the result is not yet available.
     *
     * This [StateFlow] allows observers to react to updates in the alignment result
     * as they occur.
     */
    val alignmentResult: StateFlow<MultipleAlignmentResult?> = _alignmentResult

    /**
     * Represents the score value assigned for a match in sequence alignment algorithms.
     *
     * This variable is used during alignment operations to calculate the total score
     * of aligned sequences, influencing the quality of alignment by rewarding matched
     * characters between sequences. The value of `match` is adjustable through the
     * corresponding update method.
     */
    private var match = 1
    /**
     * Represents the mismatch penalty used in sequence alignment computations.
     * A negative value is typically used to penalize mismatches during scoring.
     */
    private var  mismatch = -1
    /**
     * Represents the penalty score for introducing a gap in sequence alignment operations.
     * This value is used in algorithms such as Needleman-Wunsch and progressive alignment
     * to calculate the alignment score when a gap is introduced in the sequences.
     *
     * The gap score impacts the alignment result significantly, balancing the trade-off
     * between extending existing gaps versus creating new gaps.
     *
     * A negative value indicates a penalty, where larger negative values discourage the creation
     * of gaps during alignment.
     */
    private var gap = -1

    /**
     * Updates the current match score with the provided value.
     *
     * @param newMatch The new score to be assigned to the match.
     */
    fun updateMatchScore(newMatch: Int) {
        match = newMatch
    }
    /**
     * Updates the mismatch score with the provided value.
     *
     * @param newMismatch The new mismatch score to be set.
     */
    fun updateMismatchScore(newMismatch: Int) {
        mismatch = newMismatch
    }
    /**
     * Updates the gap score with the provided value.
     *
     * @param newGap The new value to set for the gap score.
     */
    fun updateGapScore(newGap: Int) {
        gap = newGap
    }

    /**
     * Aligns a list of sequences using progressive alignment.
     *
     * @param sequences A list of sequences to be aligned. The list must contain at least two sequences
     *                  for the alignment to be performed. If the list has fewer than two sequences,
     *                  the method will return without performing any operation.
     */
    fun alignSequences(sequences: List<String>) {
        if (sequences.size < 2) return

        val result = performProgressiveAlignment(sequences)
        _alignmentResult.value = result
    }

    /**
     * Performs progressive alignment on the provided list of sequences using the Needleman-Wunsch
     * algorithm for pairwise alignment and a center star strategy for sequence alignment.
     *
     * @param sequences The list of DNA, RNA, or protein sequences to align.
     *                  Each sequence must be a non-empty string.
     * @return A `MultipleAlignmentResult` containing the aligned sequences, overall alignment identity,
     *         total number of gaps, and alignment score.
     */
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

    /**
     * Merges an existing list of aligned sequences with a new aligned center sequence by preserving
     * alignment gaps in the resulting sequences.
     *
     * @param existing A list of previously aligned sequences to be merged.
     * @param newAlignedCenter A newly aligned center sequence containing gap positions for alignment.
     * @return A mutable list of updated sequences where the alignment with the new center is applied.
     */
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

    /**
     * Calculates the total alignment score for a given set of aligned sequences.
     * The score is calculated by iterating through each column of the alignment
     * and comparing all pairs of sequence characters. Scores for matches, mismatches,
     * and gaps are accumulated based on the provided parameters.
     *
     * @param alignedSequences A list of aligned sequences, all of the same length.
     * @param match The score to add for character matches.
     * @param mismatch The score to add for character mismatches.
     * @param gap The score to add for gaps in the alignment.
     * @return The total alignment score as an integer.
     */
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

    /**
     * Loads the first FASTA sequence from the given file.
     *
     * @param file The file containing FASTA formatted data.
     * @return The sequence of the first FASTA entry as a string.
     * @throws Exception If no sequences are found in the file.
     */
    fun loadFirstFastaSequence(file: File): String {
        val content = file.readText()
        val entries = parseFasta(content)
        if (entries.isEmpty()) throw Exception("No sequences found in file.")
        return entries[0].sequence
    }

    /**
     * Parses the input FASTA formatted string and extracts the sequences along with their headers.
     *
     * @param content The input string containing FASTA formatted data. Each sequence entry starts with
     * a header line prefixed by `>` followed by one or more lines of the sequence.
     * @return A list of FastaEntry objects, where each entry contains the header and the corresponding sequence.
     * @throws IllegalArgumentException If a sequence is empty or invalid for a given header.
     */
    fun parseFasta(content: String): List<FastaEntry> {
        return content.trim().split(">").filter { it.isNotBlank() }.map { entry ->
            val lines = entry.lines().filter { it.isNotBlank() }
            val header = lines.first().trim()
            val sequence = lines.drop(1).joinToString("").replace("\\s".toRegex(), "").uppercase()
            if (sequence.isBlank()) throw IllegalArgumentException("Empty sequence for header: $header")
            FastaEntry(header, sequence)
        }
    }

    /**
     * Exports the alignment report to a PDF file.
     *
     * The method retrieves the current alignment result, if available,
     * and passes it to the `DocumentManager` for generating a multi-sequence
     * alignment (MSA) report in PDF format.
     *
     * The operation will only proceed if a valid alignment result exists.
     *
     * Note: Ensure that required permissions are in place and the
     * `DocumentManager` is properly configured for PDF generation.
     */
    fun exportAlignmentReportToPDF() {
        alignmentResult.value?.let { result ->
            DocumentManager.generateMSAReport(result)
        }
    }
    /**
     * Exports the current alignment result to an SVG file format.
     *
     * This method generates a scalable vector graphic (SVG) representation
     * of the alignment result, which includes aligned sequences, identity percentage,
     * gap count, and alignment score. The SVG content is created using the
     * provided alignment data and then processed by the DocumentManager.
     *
     * It requires that `alignmentResult.value` contains valid data.
     */
    fun exportAlignmentImageToSVG() {
        alignmentResult.value?.let { result ->
            DocumentManager.generateAlignmentSVG(result.alignedSequences, result.identity, result.gapCount, result.score)
        }
    }

    /**
     * Displays a multiple sequence alignment (MSA) visualization based on the provided alignment result.
     * The method retrieves the currently stored alignment result and uses it to generate
     * a visualization of aligned sequences along with their identity percentage.
     *
     * This function utilizes the `DocumentManager.showMSAVisualization` method to render
     * the visualization using the aligned sequences and the calculated identity score.
     *
     * Checks if the alignment result is not null before attempting to generate the visualization.
     */
    fun showVisualization() {
        alignmentResult.value?.let { result ->
            DocumentManager.showMSAVisualization(result.alignedSequences, result.identity)
        }
    }















}
