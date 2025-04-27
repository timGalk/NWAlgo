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
        private set

    /**
     * The second sequence to be aligned.
     */
    var seq2 by mutableStateOf("")
        private set

    var match by mutableStateOf(1)
        private set
    var mismatch by mutableStateOf(-1)
        private set
    var gap by mutableStateOf(-2)
        private set

    val result: AlignmentResult
        get() = needlemanWunsch(seq1, seq2, match, mismatch, gap)

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun updateSeq1(newSeq: String?) {
        if(newSeq == null || !newSeq.all { it.isLetter() }){
            errorMessage = "Please enter a valid sequence (letters only)"
            return
        }
        seq1 = newSeq.uppercase()
        errorMessage = null


    }
    fun updateSeq2(newSeq: String?) {
        if(newSeq == null|| !newSeq.all { it.isLetter() }){
            errorMessage = "Please enter a valid sequence (letters only)"
            return
        }
        seq2 = newSeq.uppercase()
        errorMessage = null


    }

    fun updateMatch(newMatch: Int?) {
        if(newMatch == null){
            errorMessage = "Match score must be an integer"
            return
        }
        match = newMatch
        errorMessage = null
    }
    fun updateMismatch(newMismatch: Int?) {
        if(newMismatch == null){
            errorMessage = "Mismatch score must be an integer"
            return
        }
        mismatch = newMismatch
    }
    fun updateGap(newGap: Int?) {
        if(newGap == null){
            errorMessage = "Gap score must be an integer"
            return
        }
        gap = newGap
        errorMessage = null
    }



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

    /**
     * Loads the first sequence from a FASTA file and assigns it to `seq1`.
     *
     * @param file The FASTA file containing the sequence.
     * @throws Exception If no sequence is found in the file.
     */
    fun loadFastaFromFileSeq1(file: File) {
        val entries = parseFasta(file.readText())


//        if (entries.isNotEmpty()) {
//            seq1 = entries[0].sequence
//        } else {
//            throw Exception("No sequence found")
//        }
    }

    /**
     * Loads the second sequence from a FASTA file and assigns it to `seq2`.
     *
     * @param file The FASTA file containing the sequence.
     * @throws Exception If no sequence is found in the file.
     */
    fun loadFastaFromFileSeq2(file: File) {
        val entries = parseFasta(file.readText())
        if (entries.isNotEmpty()) {
            seq2 = entries[0].sequence
        } else {
            throw Exception("No sequence found")
        }
    }
    // TODO: if FASTA file contains more than one sequence -> warning to check
}
