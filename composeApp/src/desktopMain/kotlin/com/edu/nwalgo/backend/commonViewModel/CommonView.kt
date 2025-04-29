package com.edu.nwalgo.backend.commonViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.edu.nwalgo.backend.algo.AlignmentResult
import com.edu.nwalgo.backend.algo.needlemanWunsch
import com.edu.nwalgo.backend.fastaparser.FastaEntry
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

open class CommonView : ViewModel() {
    var seq1 by mutableStateOf("")
    var seq2 by mutableStateOf("")
    var match by mutableStateOf(1)
    var mismatch by mutableStateOf(-1)
    var gap by mutableStateOf(-2)

    val result: AlignmentResult
        get() = needlemanWunsch(seq1, seq2, match, mismatch, gap)



    var errorMessageSeq1 by mutableStateOf<String?>(null)
    var errorMessageSeq2 by mutableStateOf<String?>(null)
    var errorMessageMatch by mutableStateOf<String?>(null)
    var errorMessageMismatch by mutableStateOf<String?>(null)
    var errorMessageGap by mutableStateOf<String?>(null)

    fun clearErrorMessages() {
        errorMessageSeq1 = null
        errorMessageSeq2 = null
        errorMessageMatch = null
        errorMessageMismatch = null
        errorMessageGap = null
    }


    open fun updateSeq1(newSeq: String?) {

    }

    open fun updateSeq2(newSeq: String?) {

    }

    fun updateMatch(newMatch: Int?) {
        if (newMatch == null) {
            errorMessageMatch = "Match score must be an integer"
            return
        }
        match = newMatch
        errorMessageMatch = null
    }

    fun updateMismatch(newMismatch: Int?) {
        if (newMismatch == null) {
            errorMessageMismatch = "Mismatch score must be an integer"
            return
        }
        mismatch = newMismatch
        errorMessageMismatch = null
    }
    fun updateGap(newGap: Int?) {
        if (newGap == null) {
            errorMessageGap = "Gap score must be an integer"
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


    open fun loadFastaFromFileSeq1(file: File?) {

    }

    open fun loadFastaFromFileSeq2(file: File?) {}


}