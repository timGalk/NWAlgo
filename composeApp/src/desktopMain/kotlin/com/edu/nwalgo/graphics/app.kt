package com.edu.nwalgo.graphics

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.nwalgo.algo.needlemanWunsch

@Composable
@Preview

fun app(viewModel: AlignmentViewModel = remember { AlignmentViewModel() }) {
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

        ResponsiveInputRow(
            seq1 = viewModel.seq1,
            onSeq1Change = viewModel::updateSeq1,
            seq2 = viewModel.seq2,
            onSeq2Change = viewModel::updateSeq2,
            match = viewModel.match,
            onMatchChange = viewModel::updateMatch,
            mismatch = viewModel.mismatch,
            onMismatchChange = viewModel::updateMismatch,
            gap = viewModel.gap,
            onGapChange = viewModel::updateGap
        )

        Text("Pick Fasta file for first sequence:")
        Button(
            onClick = {
                val file = viewModel.pickFastaFile()
                file?.let {viewModel.loadFastaFromFileSeq1(it)}
            }
        ){
            Text("Pick File")
        }

        Text("Pick Fasta file for second sequence:")
        Button(
            onClick = {
                val file = viewModel.pickFastaFile()
                file?.let {viewModel.loadFastaFromFileSeq2(it)}
            }
        ){
            Text("Pick File")
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

    }

}



@Composable
fun ResponsiveInputRow(
    seq1: String,
    onSeq1Change: (String) -> Unit,
    seq2: String,
    onSeq2Change: (String) -> Unit,
    match: Int,
    onMatchChange: (Int) -> Unit,
    mismatch: Int,
    onMismatchChange: (Int) -> Unit,
    gap: Int,
    onGapChange: (Int) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        val isCompact = maxWidth < 700.dp
        val layout: @Composable (@Composable () -> Unit) -> Unit =
            if (isCompact) { content -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { content() } }
            else { content -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically) { content() } }

        layout {
            LabeledTextField("Seq1:", seq1, onSeq1Change)
            LabeledTextField("Seq2:", seq2, onSeq2Change)
            LabeledTextField("Match:", match.toString()) { onMatchChange(it.toIntOrNull() ?: 1) }
            LabeledTextField("Mismatch:", mismatch.toString()) { onMismatchChange(it.toIntOrNull() ?: -1) }
            LabeledTextField("Gap:", gap.toString()) { onGapChange(it.toIntOrNull() ?: -2) }
        }
    }
}

@Composable
fun LabeledTextField(label: String, value: String, onChange: (String) -> Unit) {
    Column(modifier = Modifier.widthIn(min = 100.dp, max = 160.dp)) {
        Text(label)
        TextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth()
                .height(50.dp),
        )
    }
}
