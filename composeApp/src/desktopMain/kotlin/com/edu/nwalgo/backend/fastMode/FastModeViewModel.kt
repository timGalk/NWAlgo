package com.edu.nwalgo.backend.fastMode


import com.edu.nwalgo.backend.commonViewModel.CommonView


/**
 * FastModeViewModel is responsible for handling and validating sequence inputs
 * and managing file-based sequence loading within a fast mode operation.
 * It extends from CommonView to reuse shared functionalities and state.
 */
class FastModeViewModel : CommonView() {

    /**
     * Updates the value of `seq1` with the provided input after validating it.
     * The input must contain only letters, and its length must not exceed 20 characters.
     * If the input fails validation, an appropriate error message is set.
     *
     * @param newSeq The new sequence to be assigned to `seq1`. It should be a string containing
     *               only alphabetical characters and be no more than 20 characters long. If this
     *               parameter is null or invalid, an error message is updated instead.
     */
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

    /**
     * Updates the sequence `seq2` based on the input provided.
     * Validates the input to ensure it only contains alphabetic characters and meets length constraints.
     * Converts the sequence to uppercase when valid. In case of invalid input, sets appropriate error messages.
     *
     * @param newSeq the new sequence to set for `seq2`. Can be null.
     */
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




}
