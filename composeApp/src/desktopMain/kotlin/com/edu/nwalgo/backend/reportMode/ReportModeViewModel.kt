package com.edu.nwalgo.backend.reportMode

import com.edu.nwalgo.backend.commonViewModel.CommonView

class ReportModeViewModel : CommonView() {

    override fun updateSeq1(newSeq: String?) {
        if (newSeq == null || !newSeq.all { it.isLetter() }) {
            errorMessageSeq1 = "Please enter a valid sequence (letters only)"
            return
        }
        seq1 = newSeq.uppercase()
        errorMessageSeq1 = null


    }
    override fun updateSeq2(newSeq: String?) {
        if (newSeq == null || !newSeq.all { it.isLetter() }) {
            errorMessageSeq2 = "Please enter a valid sequence (letters only)"
            return
        }
        seq2 = newSeq.uppercase()
        errorMessageSeq2 = null

    }
    fun createPicture() {
        // Implement the logic to create a picture
        renderMatrixToImage(seq1, seq2,result.scoreMatrix,result.path)
    }

}