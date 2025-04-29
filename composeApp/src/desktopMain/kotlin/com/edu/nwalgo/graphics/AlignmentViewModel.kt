package com.edu.nwalgo.graphics


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.edu.nwalgo.algo.AlignmentResult
import com.edu.nwalgo.algo.needlemanWunsch
import com.edu.nwalgo.fastaparser.FastaEntry
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

    var errorMessageSeq2 by mutableStateOf<String?>(null)
        private set
    var errorMessageSeq1 by mutableStateOf<String?>(null)
    var errorMessageMatch by mutableStateOf<String?>(null)
        private set
    var errorMessageMismatch by mutableStateOf<String?>(null)
        private set
    var errorMessageGap by mutableStateOf<String?>(null)
        private set

    fun updateSeq1(newSeq: String?) {
        if(newSeq == null || !newSeq.all { it.isLetter() }){
            errorMessageSeq1 = "Please enter a valid sequence (letters only)"
            return
        }
        seq1 = newSeq.uppercase()
        errorMessageSeq1 = null


    }
    fun updateSeq2(newSeq: String?) {
        if(newSeq == null|| !newSeq.all { it.isLetter() }){
            errorMessageSeq2 = "Please enter a valid sequence (letters only)"
            return
        }
        seq2 = newSeq.uppercase()
        errorMessageSeq2 = null


    }

    fun updateMatch(newMatch: Int?) {
        if(newMatch == null){
            errorMessageMatch = "Match score must be an integer"
            return
        }
        match = newMatch
        errorMessageMatch = null
    }
    fun updateMismatch(newMismatch: Int?) {
        if(newMismatch == null){
            errorMessageMismatch = "Mismatch score must be an integer"
            return
        }
        mismatch = newMismatch
        errorMessageMismatch = null
    }
    fun updateGap(newGap: Int?) {
        if(newGap == null){
            errorMessageGap= "Gap score must be an integer"
            return
        }
        gap = newGap
        errorMessageGap = null
    }

    fun pickFastaFile(): File {
        val fileDialog = FileDialog(Frame(), "Select FASTA file", FileDialog.LOAD)
        fileDialog.isVisible = true
        val selectedFile = fileDialog.files.firstOrNull()
        if (selectedFile != null) {
           var file = selectedFile
        } else {
            throw IllegalArgumentException("No file selected.")
        }

        return selectedFile

    }


    fun parseFasta(content: String): List<FastaEntry> {
        return content.trim().split(">").filter { it.isNotBlank() }.map { entry ->
            val lines = entry.lines().filter { it.isNotBlank() }
            if (lines.isEmpty()) throw IllegalArgumentException("Malformed FASTA: missing header or sequence.")
            val header = lines.first().trim()
            val sequence = lines.drop(1).joinToString("").replace("\\s".toRegex(), "").uppercase()
            if (sequence.isBlank()) throw IllegalArgumentException("Malformed FASTA: empty sequence for header: $header")
            FastaEntry(header, sequence)
        }
    }


    fun loadFastaFromFileSeq1(file: File?) {
        if (file == null) throw IllegalArgumentException("No file selected.")
        try {
            val entries = parseFasta(file.readText())
            if (entries.isNotEmpty()) {
                seq1 = entries[0].sequence
            } else {
                throw Exception("No sequences found in file: ${file.name}")
            }
        } catch (e: Exception) {
            throw Exception("Error while loading FASTA file '${file.name}': ${e.message}", e)
        }
    }


    fun loadFastaFromFileSeq2(file: File?) {
        if (file == null) throw IllegalArgumentException("No file selected.")
        try {
            val entries = parseFasta(file.readText())
            if (entries.isNotEmpty()) {
                seq2 = entries[0].sequence
            } else {
                throw Exception("No sequences found in file: ${file.name}")
            }
        } catch (e: Exception) {
            throw Exception("Error while loading FASTA file '${file.name}': ${e.message}", e)
        }
    }

}
