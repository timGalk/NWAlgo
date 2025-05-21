package com.edu.nwalgo.backend.multipleseqaligner

interface MultipleSequenceAligner {
    fun align(sequences: List<String>): MultipleAlignmentResult
}
