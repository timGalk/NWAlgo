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
                    sequenceInputs = sequenceInputs.toMutableList().also { it[index] = updated }
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
            val fileDialog = java.awt.FileDialog(null as java.awt.Frame?, "Select FASTA File", java.awt.FileDialog.LOAD)
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

        alignmentResult?.let { result ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("Aligned Sequences:", style = MaterialTheme.typography.h6)
            result.alignedSequences.forEach { seq ->
                Text(seq, style = MaterialTheme.typography.body2)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Identity: ${"%.2f".format(result.identity)}%")
            Text("Gaps: ${result.gapCount}")
            Text("Score: ${result.score}")

            Text("Alignment Result: ${result.alignedSequences.size}", style = MaterialTheme.typography.h6)
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


