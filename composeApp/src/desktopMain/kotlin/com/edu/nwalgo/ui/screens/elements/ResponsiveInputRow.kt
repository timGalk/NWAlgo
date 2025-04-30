package com.edu.nwalgo.ui.screens.elements

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.nwalgo.backend.commonViewModel.CommonView

/**
 * Renders a responsive input row that adapts its layout based on the available screen size.
 * On compact screens, the inputs are displayed in a vertical column layout, and on larger screens,
 * they are displayed in a horizontal row layout. The inputs are tied to a view model for state management.
 *
 * @param viewModel The view model of type CommonView, providing the state and logic for input fields and their validation.
 */
@Composable
fun ResponsiveInputRow(viewModel: CommonView) {

    BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        val isCompactLayout = maxWidth < 700.dp

        val layout: @Composable (@Composable () -> Unit) -> Unit =
            if (isCompactLayout) {
                { content -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { content() } }
            } else {
                { content -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically) { content() } }
            }

        layout {
            ValidatedTextField("Seq 1", viewModel.seq1,viewModel.errorMessageSeq1, viewModel::updateSeq1)
            ValidatedTextField("Seq 2", viewModel.seq2,viewModel.errorMessageSeq2, viewModel::updateSeq2)
            ValidatedIntField("Match", viewModel.match, viewModel.errorMessageMatch, viewModel::updateMatch)
            ValidatedIntField("Mismatch", viewModel.mismatch, viewModel.errorMessageMismatch, viewModel::updateMismatch)
            ValidatedIntField("Gap", viewModel.gap, viewModel.errorMessageGap, viewModel::updateGap)
        }
    }
}


/**
 * A composable function that displays a labeled text field with validation feedback.
 * The text field indicates whether the input is valid or invalid and shows an error message if applicable.
 *
 * @param label The label text displayed above the text field.
 * @param value The current value of the text field.
 * @param error The error message to display if the input is invalid, or null if the input is valid.
 * @param onValueChange A lambda function that is invoked when the value of the text field changes,
 * accepting the updated text as a parameter.
 */
@Composable
fun ValidatedTextField(
    label: String,
    value: String,
    error: String?,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.widthIn(min = 100.dp, max = 160.dp)) {
        Text(label)
        TextField(
            value = value,
            onValueChange = onValueChange,
            isError = error != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
        if (error != null) {
            Text(
                text = error,
                color = Color.Red,
                fontSize = 12.sp
            )
        } else{
            Text(
                text = "Valid",
                color = Color.Green,
                fontSize = 12.sp
            )
        }
    }
}
/**
 * A composable function for an input field that validates and displays integer values,
 * providing feedback on valid or invalid input.
 *
 * @param label The label text to display above the input field.
 * @param value The current integer value of the input field. Can be `null` for no value.
 * @param error The error message to display when the input is invalid. Can be `null` if there is no error.
 * @param onValueChange Callback invoked when the input value changes. Passes the new integer value or `null` if invalid.
 */
@Composable
fun ValidatedIntField(
    label: String,
    value: Int?,
    error: String?,
    onValueChange: (Int?) -> Unit
) {
    var text by remember { mutableStateOf(value?.toString() ?: "") }

    LaunchedEffect(value) {
        if (value?.toString() != text) {
            text = value?.toString() ?: ""
        }
    }

    Column(modifier = Modifier.widthIn(min = 100.dp, max = 160.dp)) {
        Text(label)
        TextField(
            value = text,
            onValueChange = { input ->
                text = input
                val intValue = input.toIntOrNull()
                onValueChange(intValue)
            },
            isError = error != null || text.isEmpty(), // mark empty field also as error
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
        if (error != null || text.isEmpty()) {
            Text(
                text = error ?: "Value required",
                color = Color.Red,
                fontSize = 12.sp
            )
        } else {
            Text(
                text = "Valid",
                color = Color.Green,
                fontSize = 12.sp
            )
        }
    }
}
