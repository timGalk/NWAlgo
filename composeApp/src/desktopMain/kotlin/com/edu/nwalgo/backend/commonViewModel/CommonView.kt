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

/**
 * A base class used for managing sequence alignment operations and configurations,
 * which are applied to Needleman-Wunsch algorithm calculations.
 * It maintains both mutable properties and error handling for sequence inputs and scoring parameters.
 */
open class CommonView : ViewModel() {
    /**
     * Represents the first sequence input by the user in the Needleman-Wunsch algorithm visualizer.
     * This variable is backed by a mutable state and is observed for changes to update UI components and
     * perform validations.
     *
     * - The sequence must consist of alphabetical characters only.
     * - It is automatically converted to uppercase upon assignment.
     * - The sequence length must not exceed 20 characters in quick mode.
     *
     * Errors or validations associated with `seq1` are managed through a corresponding error message variable.
     */
    var seq1 by mutableStateOf("")

    /**
     * Represents the second sequence input by the user for alignment operations.
     *
     * This variable holds the user-provided sequence `seq2`, which is validated
     * to ensure it contains only alphabetic characters and adheres to a maximum
     * length constraint. Upon validation, the sequence is converted to uppercase.
     *
     * Used in alignment visualizations and computations, ensuring correctness and
     * consistency for downstream operations.
     */
    var seq2 by mutableStateOf("")

    /**
     * Represents the match score used in the sequence alignment process.
     *
     * This variable defines the scoring value when characters in both sequences match
     * during the alignment. It is mutable, allowing for updates or changes before
     * the alignment operation is performed.
     */
    var match by mutableStateOf(1)

    /**
     * Represents the mismatch score used in sequence alignment.
     *
     * This variable determines the penalty for mismatched characters between
     * two sequences during alignment. A negative value indicates a penalty,
     * while a higher absolute value results in a stronger penalty applied
     * when characters in the sequences do not match.
     *
     * By default, the mismatch score is set to -1. It can be updated via
     * the `updateMismatch` function within the associated ViewModel or class.
     * If an invalid value is provided, an error message is generated.
     */
    var mismatch by mutableStateOf(-1)

    /**
     * Represents the penalty or score associated with introducing a gap in the alignment
     * of two sequences. This value is used during the Needleman-Wunsch alignment process
     * to calculate the optimal sequence alignment. Negative values are typically used
     * to represent penalties for gaps.
     */
    var gap by mutableStateOf(-2)

    /**
     * Stores the result of sequence alignment computed using the Needleman-Wunsch algorithm.
     *
     * The result represents the optimal alignment between two input sequences (`seq1` and `seq2`)
     * based on the provided scoring parameters: `match`, `mismatch`, and `gap`.
     *
     * This property retrieves the alignment result by invoking the Needleman-Wunsch algorithm
     * with the specified input sequences and scoring values.
     */
    val result: AlignmentResult
        get() = needlemanWunsch(seq1, seq2, match, mismatch, gap)


    /**
     * Holds the error message related to the validation of the first sequence input (`seq1`).
     *
     * This variable is updated dynamically based on input validation in methods like `updateSeq1`.
     * If the input is valid, the value is set to `null`. Otherwise, an appropriate error message
     * indicating the specific validation issue (e.g., invalid characters or excessive length) is stored.
     *
     * It is used to display contextual error messages in the UI, ensuring immediate feedback to the user.
     */
    var errorMessageSeq1 by mutableStateOf<String?>(null)

    /**
     * Represents the error message associated with the sequence input `seq2`.
     * This property is updated to provide feedback when invalid input is detected
     * during validation in sequence-related operations (e.g., `updateSeq2`).
     *
     * A null value indicates that there are no errors currently associated with `seq2`.
     * When the input fails validation, it is set to an appropriate error message,
     * such as:
     * - "Please enter a valid sequence (letters only)" if the input contains non-alphabetic characters.
     * - "Sequence is too long. Use report mode for longer sequences." if the sequence exceeds the length constraints.
     */
    var errorMessageSeq2 by mutableStateOf<String?>(null)

    /**
     * Represents an error message related to the "match" input parameter of the alignment process.
     *
     * This variable holds an error message as a nullable string when the "match" value is invalid, for example,
     * when it is not an integer. Otherwise, it is set to null to indicate no errors.
     *
     * The value of this variable is updated within related methods, such as `updateMatch`,
     * to provide validation feedback to the user interface.
     */
    var errorMessageMatch by mutableStateOf<String?>(null)

    /**
     * Holds the error message related to the mismatch input field.
     *
     * This variable is used to store and manage validation error messages
     * specific to the mismatch score input. It is null by default, indicating
     * no error. When validation fails, a descriptive error message is assigned
     * to guide the user in correcting the input.
     */
    var errorMessageMismatch by mutableStateOf<String?>(null)

    /**
     * Represents an error message related to the gap score input in the sequence alignment parameters.
     *
     * This variable holds a nullable string, indicating any validation error linked to the gap score.
     * It is used to provide feedback to the user when the gap score input is invalid.
     * - A non-null value denotes the specific error message describing the issue.
     * - A null value indicates that there is no error with the gap score input.
     *
     * This property is typically updated during input validation processes, such as in the
     * `updateGap` method where it validates if the gap score is an integer. It is also cleared
     * in methods like `clearErrorMessages` to reset any error states.
     */
    var errorMessageGap by mutableStateOf<String?>(null)

    /**
     * Resets all error message fields to null.
     *
     * This method clears any previously stored error messages related to
     * sequence matching, mismatching, and gaps. It ensures that all error
     * message fields (`errorMessageSeq1`, `errorMessageSeq2`,
     * `errorMessageMatch`, `errorMessageMismatch`, `errorMessageGap`) are reset
     * to a null state, effectively removing any existing error data.
     */
    fun clearErrorMessages() {
        errorMessageSeq1 = null
        errorMessageSeq2 = null
        errorMessageMatch = null
        errorMessageMismatch = null
        errorMessageGap = null
    }


    /**
     * Updates the sequence with the provided new sequence value.
     *
     * @param newSeq the new sequence to update with. It can be null to indicate no update.
     */
    open fun updateSeq1(newSeq: String?) {

    }

    /**
     * Updates the sequence with the provided new value.
     *
     * @param newSeq The new sequence to update with. Can be null.
     */
    open fun updateSeq2(newSeq: String?) {

    }

    /**
     * Updates the match score with the given value.
     * If the provided value is null, sets an error message indicating that
     * the match score must be an integer. Otherwise, updates the match field
     * and clears any error message related to the match score.
     *
     * @param newMatch The new match score to be set, or null if no valid value is provided.
     */
    fun updateMatch(newMatch: Int?) {
        if (newMatch == null) {
            errorMessageMatch = "Match score must be an integer"
            return
        }
        match = newMatch
        errorMessageMatch = null
    }

    /**
     * Updates the mismatch score for sequence alignment and validates the input.
     *
     * If the provided mismatch score is null, an error message will be set.
     * Otherwise, the mismatch score is updated and the corresponding error message is cleared.
     *
     * @param newMismatch The new mismatch score to be set. If null, an error message is triggered.
     */
    fun updateMismatch(newMismatch: Int?) {
        if (newMismatch == null) {
            errorMessageMismatch = "Mismatch score must be an integer"
            return
        }
        mismatch = newMismatch
        errorMessageMismatch = null
    }

    /**
     * Updates the value of the gap parameter used in sequence alignment. If the input is null, an error
     * message will be set indicating that the gap score must be an integer.
     *
     * @param newGap The new gap value to be set. If null, an error message will be displayed and the
     *               gap value will not be updated.
     */
    fun updateGap(newGap: Int?) {
        if (newGap == null) {
            errorMessageGap = "Gap score must be an integer"
            return
        }
        gap = newGap
        errorMessageGap = null
    }

    /**
     * Opens a file dialog for the user to select a FASTA file from the file system.
     * The method allows the user to browse their files and pick a single*/
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

    /**
     * Parses the content of a FASTA formatted string and extracts sequences into a list of `FastaEntry` objects.
     *
     * @param content The string containing FASTA formatted data. Each entry should start with a header line
     *                beginning with ">", followed by one or more lines of nucleotide or protein sequences.
     * @return A list of `FastaEntry` objects, where each entry contains a header and its corresponding sequence.
     *         The sequence is normalized to uppercase and stripped of whitespace.
     * @throws IllegalArgumentException If the content is malformed (e.g., missing headers, empty sequences, or invalid format).
     */
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


    /**
     * Loads a sequence from a given FASTA file and updates the first sequence (Seq1) if valid data is found.
     *
     * @param file The FASTA file to load and parse. Throws an exception if the file is null, contains no sequences,
     * or cannot be properly parsed. File contents are expected to follow the FASTA format.
     */
    fun loadFastaFromFileSeq1(file: File?) {
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


    /**
     * Loads a FASTA file, parses its contents, and updates the sequence for Seq2.
     * Validates that the passed file is not null and ensures that the file contains
     * at least one valid sequence. If any error occurs during reading or parsing
     * the file, an exception is thrown.
     *
     * @param file The FASTA file to be loaded. Must not be null.
     * @throws IllegalArgumentException If the file parameter is null.
     * @throws Exception If no sequences are found in the file or if an error occurs during file processing.
     */
    fun loadFastaFromFileSeq2(file: File?) {
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