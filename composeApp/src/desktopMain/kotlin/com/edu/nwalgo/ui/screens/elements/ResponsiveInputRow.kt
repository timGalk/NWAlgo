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
