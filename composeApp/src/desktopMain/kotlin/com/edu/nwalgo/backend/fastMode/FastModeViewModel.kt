package com.edu.nwalgo.backend.fastMode


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.edu.nwalgo.backend.algo.AlignmentResult
import com.edu.nwalgo.backend.algo.needlemanWunsch
import com.edu.nwalgo.backend.commonViewModel.CommonView
import com.edu.nwalgo.backend.fastaparser.FastaEntry
import java.awt.FileDialog
import java.awt.Frame
import java.io.File


/**
 * ViewModel for managing the state and logic of the Needleman-Wunsch alignment algorithm.
 * This ViewModel holds the input sequences, scoring parameters, and computes the alignment result.
 */
class FastModeViewModel : CommonView() {

    override fun updateSeq1(newSeq: String?) {
        if (newSeq == null || !newSeq.all { it.isLetter() }) {
            errorMessageSeq1 = "Please enter a valid sequence (letters only)"
            return
        } else if (newSeq.length > 20) {
            errorMessageSeq1 = "Sequence is too long. Use report mode for longer sequences."
            return
        }
        seq1 = newSeq.uppercase()
        errorMessageSeq1 = null


    }

    override fun updateSeq2(newSeq: String?) {
        if (newSeq == null || !newSeq.all { it.isLetter() }) {
            errorMessageSeq2 = "Please enter a valid sequence (letters only)"
            return
        } else if (newSeq.length > 20) {
            errorMessageSeq2 = "Sequence is too long. Use report mode for longer sequences."
            return
        }
        seq2 = newSeq.uppercase()
        errorMessageSeq2 = null


    }


    override fun loadFastaFromFileSeq1(file: File?) {
        if (file == null) throw IllegalArgumentException("No file selected.")
        try {
            val entries = parseFasta(file.readText())
            if (entries.isNotEmpty()) {
                updateSeq1(entries[0].sequence)
            } else {
                throw Exception("No sequences found in file: ${file.name}")
            }
        } catch (e: Exception) {
            throw Exception("Error while loading FASTA file '${file.name}': ${e.message}", e)
        }
    }


    override fun loadFastaFromFileSeq2(file: File?) {
        if (file == null) throw IllegalArgumentException("No file selected.")
        try {
            val entries = parseFasta(file.readText())
            if (entries.isNotEmpty()) {
                updateSeq2(entries[0].sequence)
            } else {
                throw Exception("No sequences found in file: ${file.name}")
            }
        } catch (e: Exception) {
            throw Exception("Error while loading FASTA file '${file.name}': ${e.message}", e)
        }
    }

}
