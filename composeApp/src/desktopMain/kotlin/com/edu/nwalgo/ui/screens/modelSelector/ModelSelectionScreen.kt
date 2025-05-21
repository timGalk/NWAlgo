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

/**
 * Displays a screen for selecting an alignment mode. The user can choose between
 * "Quick Visual Mode" and "Report Mode" by clicking on the respective options.
 *
 * @param onModeSelected A callback function that is triggered when a mode is selected.
 * It receives the chosen alignment mode as an instance of the `AlignmentMode` enum.
 */
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

        ModeOption(
            title = "Multiple Sequence Alignment (MSA)",
            description = "Align multiple sequences simultaneously.",
            onClick = { onModeSelected(AlignmentMode.MSA) }
        )
    }
}

/**
 * Composable function that represents a selectable mode option with a title and description.
 *
 * @param title The title of the option.
 * @param description A brief description of the option.
 * @param onClick A lambda function to be invoked when the option is clicked.
 */
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


/**
 * Defines the modes of alignment available within the application.
 *
 * This enum class is used to specify the selected alignment mode, which dictates
 * the behavior and functionality of various screens in the application, such as
 * visualization or report generation.
 */
enum class AlignmentMode {
    /**
     * Represents a visual component in a graphical user interface with a specific alignment mode.
     *
     * This class is used to configure and display a visual screen with the given alignment behavior.
     * It inherits from the `Screen` base class, utilizing the specified `AlignmentMode` to
     * dictate how the screen should align its contents.
     *
     * @property mode The alignment mode used to determine the behavior of the visual layout.
     */
    QuickVisual,
    /**
     * A data class that represents a report within the application's user interface.
     * It is part of the application's navigation system and extends the `Screen` class.
     *
     * The `Report` class employs an `AlignmentMode` to specify the alignment behavior
     * associated with the report screen. This mode influences the layout and alignment
     * settings relevant to the context of the report.
     *
     * @property mode The alignment mode used to determine how content within the report
     * is displayed or aligned.
     */
    Report,

    /**
     * Represents a multiple sequence alignment (MSA) mode.
     *
     * This mode is used for aligning multiple sequences simultaneously.
     */
    MSA
}
