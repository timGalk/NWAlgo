package com.edu.nwalgo.backend.reportMode

import DocumentManager
import com.edu.nwalgo.backend.commonViewModel.CommonView
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ReportModeViewModel : CommonView() {

    val documentManager = DocumentManager()

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

    fun exportAlignmentReportToPDF() {
        documentManager.exportAlignmentReportToPDF(result)
    }
    fun exportAlignmentImageToPNG() {
       documentManager.exportMatrixImageToFile(seq1, seq2,result.scoreMatrix, result.path)
    }



}