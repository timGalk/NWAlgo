import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModelSelectionScreen(onModeSelected: (AlignmentMode) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Choose Alignment Mode", fontSize = 24.sp)

        ModeOption(
            title = "Quick Visual Mode",
            description = "Ideal for short sequences. Interactive visual matrix and alignment animation.",
            onClick = { onModeSelected(AlignmentMode.QuickVisual) }
        )

        ModeOption(
            title = "Report Mode",
            description = "For long sequences. Generates an image of the matrix and a report file.",
            onClick = { onModeSelected(AlignmentMode.Report) }
        )
    }
}

@Composable
fun ModeOption(title: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        elevation = 4.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, fontSize = 14.sp, color = MaterialTheme.colors.secondary)
        }
    }
}


enum class AlignmentMode {
    QuickVisual,
    Report
}
