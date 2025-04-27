package com.edu.nwalgo.graphics.elements

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.nwalgo.graphics.AlignmentViewModel

@Composable
fun ResponsiveInputRow(viewModel: AlignmentViewModel) {

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
            ValidatedTextField("Seq 1", viewModel.seq1,viewModel.errorMessage, viewModel::updateSeq1)
            ValidatedTextField("Seq 2", viewModel.seq2,viewModel.errorMessage, viewModel::updateSeq2)
            ValidatedIntField("Match", viewModel.match, viewModel.errorMessage, viewModel::updateMatch)
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
    value: Int,
    error: String?,
    onValueChange: (Int?) -> Unit
) {
    Column(modifier = Modifier.widthIn(min = 100.dp, max = 160.dp)) {
        Text(label)
        TextField(
            value = value.toString(),
            onValueChange = { input ->
                val intValue = input.toIntOrNull()
                onValueChange(intValue)
            },
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
        } else {
            Text(
                text = "Valid",
                color = Color.Green,
                fontSize = 12.sp
            )
        }
    }
}