package com.edu.nwalgo.graphics


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.edu.nwalgo.algo.AlignmentResult
import com.edu.nwalgo.algo.needlemanWunsch
import com.edu.nwalgo.fastaparser.parseFasta
import java.awt.FileDialog
import java.awt.Frame
import java.io.File


/**
 * ViewModel for managing the state and logic of the Needleman-Wunsch alignment algorithm.
 * This ViewModel holds the input sequences, scoring parameters, and computes the alignment result.
 */
class AlignmentViewModel : ViewModel() {

    /**
     * The first sequence to be aligned.
     */
    var seq1 by mutableStateOf("")

    /**
     * The second sequence to be aligned.
     */
    var seq2 by mutableStateOf("")

    /**
     * The score for a match between characters in the sequences.
     */
    var match by mutableStateOf(1)

    /**
     * The penalty for a mismatch between characters in the sequences.
     */
    var mismatch by mutableStateOf(-1)

    /**
     * The penalty for introducing a gap in the alignment.
     */
    var gap by mutableStateOf(-2)

    /**
     * Computes the alignment result using the Needleman-Wunsch algorithm.
     *
     * @return The result of the alignment, including the score matrix, aligned sequences, and other details.
     */
    val result: AlignmentResult
        get() = needlemanWunsch(seq1, seq2, match, mismatch, gap)

    /**
     * Updates the first sequence to be aligned.
     * Converts the input to uppercase for consistency.
     *
     * @param value The new value for the first sequence.
     */
    fun updateSeq1(value: String) { seq1 = value.uppercase() }

    /**
     * Updates the second sequence to be aligned.
     * Converts the input to uppercase for consistency.
     *
     * @param value The new value for the second sequence.
     */
    fun updateSeq2(value: String) { seq2 = value.uppercase() }

    /**
     * Updates the match score.
     *
     * @param value The new match score.
     */
    fun updateMatch(value: Int) { match = value }

    /**
     * Updates the mismatch penalty.
     *
     * @param value The new mismatch penalty.
     */
    fun updateMismatch(value: Int) { mismatch = value }

    /**
     * Updates the gap penalty.
     *
     * @param value The new gap penalty.
     */
    fun updateGap(value: Int) { gap = value }
    /**
     * Loads sequences from a FASTA file.
     * The first sequence is assigned to `seq1`, and the second (if present) to `seq2`.
     *
     * @param file The FASTA file containing the sequences.
     */
    fun pickFastaFile(): File? {
        val dialog = FileDialog(Frame(), "Choose FASTA File", FileDialog.LOAD)
        dialog.isVisible = true
        return dialog.files.firstOrNull()
    }

    fun loadFastaFromFile(file: File) {
        val entries = parseFasta(file.readText())
        if (entries.isNotEmpty()) {
            seq1 = entries[0].sequence
            if (entries.size > 1) {
                seq2 = entries[1].sequence
            }
        }
    }
}
