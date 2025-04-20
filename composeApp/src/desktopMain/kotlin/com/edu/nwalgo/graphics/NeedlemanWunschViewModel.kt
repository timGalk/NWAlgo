package com.edu.nwalgo.graphics


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.edu.nwalgo.algo.AlignmentResult
import com.edu.nwalgo.algo.needlemanWunsch

class AlignmentViewModel : ViewModel() {
    var seq1 by mutableStateOf("")
    var seq2 by mutableStateOf("")
    var match by mutableStateOf(1)
    var mismatch by mutableStateOf(-1)
    var gap by mutableStateOf(-2)

    val result: AlignmentResult
        get() = needlemanWunsch(seq1, seq2, match, mismatch, gap)

    fun updateSeq1(value: String) { seq1 = value.uppercase() }
    fun updateSeq2(value: String) { seq2 = value.uppercase() }
    fun updateMatch(value: Int) { match = value }
    fun updateMismatch(value: Int) { mismatch = value }
    fun updateGap(value: Int) { gap = value }
}

