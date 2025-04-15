package com.edu.nwalgo.graphics

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
fun App() {
    var seq1 by remember { mutableStateOf("") }
    var seq2 by remember { mutableStateOf("") }

    val result = needlemanWunsch(seq1, seq2 , 1, -1, -2)

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Needleman-Wunsch Visualizer", fontSize = 24.sp)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Seq1:")
            Spacer(Modifier.width(8.dp))
            TextField(seq1, onValueChange = { seq1 = it })
            Spacer(Modifier.width(16.dp))
            Text("Seq2:")
            Spacer(Modifier.width(8.dp))
            TextField(seq2, onValueChange = { seq2 = it })
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
    }
}