package com.edu.nwalgo.ui.screens.msaMode

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.edu.nwalgo.backend.msa.MSAModeViewModel
import java.awt.FileDialog
import java.awt.Frame


/**
 * A composable function for Multiple Sequence Alignment (MSA) mode in the application.
 * This screen allows users to input or upload sequences, configure scoring parameters,
 * perform sequence alignment, and visualize or export alignment results.
 *
 * @param viewModel An instance of [MSAModeViewModel] used to handle the alignment logic and manage state.
 *                  Defaults to a newly initialized [MSAModeViewModel] with `remember`.
 * @param onBack A lambda function to handle the back navigation when the "Back" button is clicked.
 */
@Composable
fun MSAmode(viewModel: MSAModeViewModel = remember { MSAModeViewModel() }, onBack: () -> Unit) {
    val alignmentResult by viewModel.alignmentResult.collectAsState()
    var sequenceInputs by remember { mutableStateOf(listOf(TextFieldValue())) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var gapPenalty by remember { mutableStateOf(-2) }
    var matchScore by remember { mutableStateOf(1) }
    var mismatchScore by remember { mutableStateOf(-1) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Multiple Sequence Alignment", style = MaterialTheme.typography.h4)

        sequenceInputs.forEachIndexed { index, value ->
            OutlinedTextField(
                value = value,
                onValueChange = { updated ->
                    val upperText = updated.text.uppercase()
                    val isValid = upperText.all { it.isLetter() || it == '-' }

                    if (isValid) {
                        sequenceInputs = sequenceInputs.toMutableList().also {
                            it[index] = updated.copy(text = upperText)
                        }
                    } else {
                        showErrorDialog = true
                        errorMessage = "Only letters A-Z and '-' are allowed."
                    }
                },
                label = { Text("Sequence ${index + 1}") },
                modifier = Modifier.fillMaxWidth()
            )

        }

        Button(onClick = {
            sequenceInputs = sequenceInputs + TextFieldValue()
        }) {
            Text("+ Add Sequence")
        }
        Text("Or")

        Button(onClick = {
            val fileDialog = FileDialog(null as Frame?, "Select FASTA File", FileDialog.LOAD)
            fileDialog.isVisible = true
            val file = fileDialog.files.firstOrNull()
            if (file != null) {
                val fastaSequence = viewModel.loadFirstFastaSequence(file)
                sequenceInputs = sequenceInputs + TextFieldValue(fastaSequence)
            }
        }) {
            Text(" + Load FASTA File")
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ScoreControl(
                    label = "Match",
                    value = matchScore,
                    onChange = {
                        matchScore = it
                        viewModel.updateMatchScore(it)
                    },
                    modifier = Modifier.weight(1f)
                )
                ScoreControl(
                    label = "Mismatch",
                    value = mismatchScore,
                    onChange = {
                        mismatchScore = it
                        viewModel.updateMismatchScore(it)
                    },
                    modifier = Modifier.weight(1f)
                )
                ScoreControl(
                    label = "Gap Penalty",
                    value = gapPenalty,
                    onChange = {
                        gapPenalty = it
                        viewModel.updateGapScore(it)
                    },
                    modifier = Modifier.weight(1f)
                )
            }


        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Before every calculation, please make sure that the sequences are in the same format and contain only letters A-Z and '-'." +
                    "\n" + "And click Align to start the alignment process."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                val sequences = sequenceInputs.map { it.text.trim() }.filter { it.isNotEmpty() }
                if (sequences.size >= 2) {
                    viewModel.alignSequences(sequences)
                } else {
                    showErrorDialog = true
                    errorMessage = "Enter at least two sequences."
                }
            }) {
                Text("Align")
            }


        }
        if (alignmentResult != null) {
            Text("Alignment Result:", style = MaterialTheme.typography.h6)
            alignmentResult?.alignedSequences?.forEachIndexed { index, sequence ->
                Text("Sequence ${index + 1}: $sequence")
            }
            Text("Identity: %.2f%%".format(alignmentResult?.identity))
            Text("Gaps: ${alignmentResult?.gapCount}, Final Score: ${alignmentResult?.score}")
        } else {
            Text("No alignment result available.")
        }

        Button(onClick = {
            alignmentResult?.alignedSequences?.let {
                viewModel.exportAlignmentImageToSVG()
            } ?: run {
                showErrorDialog = true
                errorMessage = "No alignment result available."
            }

        }) {
            Text("Save Alignment Image")
        }

        Button(onClick = {
            alignmentResult?.let { result ->
                viewModel.showVisualization()
            } ?: run {
                showErrorDialog = true
                errorMessage = "No alignment result available."
            }
        }) {
            Text("Show MSA Visualization")
        }

        Button(onClick = {
            alignmentResult?.let { result ->
                viewModel.exportAlignmentReportToPDF()
            } ?: run {
                showErrorDialog = true
                errorMessage = "No alignment result available."
            }
        }) {
            Text("Crate Report")
        }


        Button(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
            Text("Back")
        }


        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Error") },
                text = { Text(errorMessage) },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

/**
 * A composable function for controlling and displaying a score with increment and decrement options.
 *
 * @param label The label associated with the score control.
 * @param value The current value of the score.
 * @param onChange A lambda function invoked when the score value changes. Provides the new score value.
 * @param modifier A [Modifier] to be applied to this layout, allowing for customization.
 */
@Composable
fun ScoreControl(
    label: String,
    value: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, style = MaterialTheme.typography.subtitle2)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { onChange(value - 1) }) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
            }
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.Center
            )
            IconButton(onClick = { onChange(value + 1) }) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
            }
        }


    }
}









