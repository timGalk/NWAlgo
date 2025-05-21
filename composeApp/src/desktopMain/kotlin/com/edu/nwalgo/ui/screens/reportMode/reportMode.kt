package com.edu.nwalgo.ui.screens.reportMode

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.nwalgo.backend.reportMode.ReportModeViewModel
import com.edu.nwalgo.ui.screens.elements.ResponsiveInputRow

/**
 * Displays the Report Mode screen for the Needleman-Wunsch Visualizer. This composable allows users
 * to input sequences or load them from FASTA files, modify scoring parameters, view alignment results,
 * and export the results as an image or a PDF report. Additional dialogs are shown for user feedback
 * (e.g., file errors, successful exports).
 *
 * @param viewModel The ViewModel instance that manages the state and logic of the report mode. Defaults to a new instance of ReportModeViewModel.
 * @param onBack The callback function to be executed when the back button is clicked. Defaults to an empty lambda.
 */
@Composable
@Preview

fun reportMode(
    viewModel: ReportModeViewModel = remember { ReportModeViewModel() },
    onBack: () -> Unit = {}

) {
    val result = viewModel.result
    var showImageSavedDialog by remember { mutableStateOf(false) }
    var showPdfExportedDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Needleman-Wunsch Visualizer Report Mode ", fontSize = 24.sp)
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

        Text("Alignment Result:", fontSize = 18.sp)
        Text(result.alignedSeq1)
        Text(result.alignedSeq2)
        Text("Identity: %.2f%%".format(result.identityPercent))
        Text("Gaps: ${result.gapCount}, Final Score: ${result.score}")
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

            // Button to save the alignment image
            Button(onClick = {
                viewModel.exportAlignmentImageToPNG()
                showImageSavedDialog = true // Show dialog after image save action
            }) {
                Text("Save Image")
            }

            // Conditionally display the "Image Saved" dialog
            if (showImageSavedDialog) {
                AlertDialog(
                    onDismissRequest = { showImageSavedDialog = false },
                    title = { Text("Image Saved") },
                    text = { Text("The image has been saved successfully. Find it in the results folder.") },
                    confirmButton = {
                        Button(onClick = { showImageSavedDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }

            // Button to export the PDF report
            Button(onClick = {
                viewModel.exportAlignmentReportToPDF()
                showPdfExportedDialog = true // Show dialog after PDF export action
            }) {
                Text("Export PDF Report")
            }

            // Conditionally display the "PDF Exported" dialog
            if (showPdfExportedDialog) {
                AlertDialog(
                    onDismissRequest = { showPdfExportedDialog = false },
                    title = { Text("PDF Report Exported") },
                    text = { Text("The PDF report has been exported successfully. Find it in the results folder.") },
                    confirmButton = {
                        Button(onClick = { showPdfExportedDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }

        Button(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
            Text("Back")
        }

    }
}


