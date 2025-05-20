package com.edu.nwalgo.ui.screens.msaMode

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.edu.nwalgo.backend.msa.MSAModeViewModel


@Composable
fun MSAmode(viewModel: MSAModeViewModel = remember { MSAModeViewModel() }, onBack: () -> Unit) {
    val alignmentResult by viewModel.alignmentResult.collectAsState()
    var sequenceInputs by remember { mutableStateOf(listOf(TextFieldValue())) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Multiple Sequence Alignment", style = MaterialTheme.typography.h3)

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sequenceInputs.size) { index ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = sequenceInputs[index],
                        onValueChange = { updated ->
                            sequenceInputs = sequenceInputs.toMutableList().also { it[index] = updated }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .height(56.dp)
                            .border(1.dp, MaterialTheme.colors .primary, MaterialTheme.shapes.medium)
                            .padding(8.dp)
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                sequenceInputs = sequenceInputs + TextFieldValue()
            }) {
                Text("+ Add Sequence")
            }

            Button(onClick = {
                val sequences = sequenceInputs.map { it.text.trim() }.filter { it.isNotEmpty() }
                viewModel.alignSequences(sequences)
            }) {
                Text("Align")
            }
        }

        alignmentResult?.let { result ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("Aligned Sequences:", style = MaterialTheme.typography.h4)
            result.alignedSequences.forEach { seq ->
                Text(seq, style = MaterialTheme.typography.body2)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Identity: ${"%.2f".format(result.identity)}%")
            Text("Gaps: ${result.gapCount}")
            Text("Score: ${result.score}")
        }
    }
}


