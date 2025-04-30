package com.edu.nwalgo.ui.screens.fastMode

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.nwalgo.backend.fastMode.FastModeViewModel
import com.edu.nwalgo.ui.screens.elements.ResponsiveInputRow

/**
 * A composable function designed for fast sequence alignment and visualization using the Needleman-Wunsch algorithm.
 * This function integrates with a `FastModeViewModel` to manage sequence inputs, validate parameters,
 * and handle file loading. It also provides a visual representation of the computed score matrix
 * and alignment results.
 *
 * @param viewModel The view model of type `FastModeViewModel` that manages the state and logic for the view.
 *                  Defaults to a new instance created with `remember`.
 * @param onBack A callback function triggered when the "Back" button is clicked. Defaults to an empty lambda.
 */
@Preview
@Composable
fun fastMode(
    viewModel:FastModeViewModel = remember { FastModeViewModel() },
    onBack: () -> Unit = {}

) {
    val result = viewModel.result

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
    ) {
        Text("Needleman-Wunsch Visualizer", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Type you sequences(or choose a FASTA file)  and parameters to start ")

        ResponsiveInputRow(viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        var isErrorDialogVisible by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }

        Button(
            onClick = {
                try {
                    val file = viewModel.pickFastaFile()
                    file.let { viewModel.loadFastaFromFileSeq1(it) }
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Unknown error"
                    isErrorDialogVisible = true
                }
            }
        ) {
            Text("Pick File for Seq1")
        }

        if (isErrorDialogVisible) {
            AlertDialog(
                onDismissRequest = { isErrorDialogVisible = false },
                title = { Text("Error") },
                text = { Text("Failed to load file: $errorMessage") },
                confirmButton = {
                    Button(onClick = { isErrorDialogVisible = false }) {
                        Text("OK")
                    }
                }
            )
        }
        Button(
            onClick = {
                try {
                    val file = viewModel.pickFastaFile()
                    file.let { viewModel.loadFastaFromFileSeq2(it) }
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Unknown error"
                    isErrorDialogVisible = true
                }
            }
        ) {
            Text("Pick File for Seq2")
        }


        Spacer(Modifier.height(24.dp))
        Text("Score Matrix:", fontSize = 18.sp)
        LazyVerticalGrid(
            columns = GridCells.Fixed(result.scoreMatrix[0].size),
            modifier = Modifier.heightIn(max = 400.dp).padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            result.scoreMatrix.forEachIndexed { i, row ->
                row.forEachIndexed { j, cell ->
                    item {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    if (result.path.contains(i to j)) Color.Green.copy(alpha = 0.3f) else Color.LightGray
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(cell.toString(), fontSize = 14.sp)
                        }
                    }
                }
            }
        }



        Spacer(Modifier.height(16.dp))
        Text("Alignment Result:", fontSize = 18.sp)
        Text(result.alignedSeq1)
        Text(result.alignedSeq2)
        Text("Identity: %.2f%%".format(result.identityPercent))
        Text("Gaps: ${result.gapCount}, Final Score: ${result.score}")
        Spacer(Modifier.height(16.dp))
        Button(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
            Text("Back")
        }


    }

}
