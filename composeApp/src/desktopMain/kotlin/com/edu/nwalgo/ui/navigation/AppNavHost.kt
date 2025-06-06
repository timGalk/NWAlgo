import androidx.compose.runtime.*
import com.edu.nwalgo.ui.screens.fastMode.fastMode
import com.edu.nwalgo.ui.screens.msaMode.MSAmode
import com.edu.nwalgo.ui.screens.reportMode.reportMode

/**
 * A composable function that serves as the main navigation host for the application.
 * It manages navigation between different screens based on the current application state.
 *
 * The navigation logic is handled through a state variable `currentScreen`, which is initialized
 * to the `ModelSelection` screen and is updated as the user interacts with the application.
 *
 * Screens handled by this navigation host include:
 * - ModelSelectionScreen: Allows the user to select a mode and updates the `currentScreen` state
 *   based on the selected alignment mode.
 * - QuickVisual: Represents the quick visual alignment mode. Provides an onBack handler to navigate
 *   back to the ModelSelectionScreen.
 * - Report: Represents the report mode. Provides an onBack handler to navigate back to the ModelSelectionScreen.
 */
@Composable
fun AppNavHost() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.ModelSelection) }

    when (val screen = currentScreen) {
        is Screen.ModelSelection -> {
            ModelSelectionScreen { selectedMode ->
                currentScreen = when (selectedMode) {
                    AlignmentMode.QuickVisual -> Screen.QuickVisual(selectedMode)
                    AlignmentMode.Report -> Screen.Report(selectedMode)
                    AlignmentMode.MSA -> Screen.MSA(selectedMode)
                }
            }
        }


        is Screen.QuickVisual -> {
            fastMode(onBack = { currentScreen = Screen.ModelSelection })
        }

        is Screen.Report -> {
            reportMode(onBack = { currentScreen = Screen.ModelSelection })
        }

        is Screen.MSA -> {MSAmode(onBack = { currentScreen = Screen.ModelSelection })}

        else -> {}
    }
}

/**
 * Represents the base class for different screens within the application.
 * Serves as a sealed class to encapsulate various screen states or types.
 */
sealed class Screen {
    /**
     * ModelSelection is an object that extends the Screen class.
     *
     * This object is used to represent a specific screen in the application's navigation flow
     * where a user can interact with and select a model or configuration.
     *
     * It acts as a centralized point for managing the state and behavior associated with
     * the model selection process.
     */
    object ModelSelection : Screen()
    /**
     * Represents a visual component in a graphical user interface with a specific alignment mode.
     *
     * This class is used to configure and display a visual screen with the given alignment behavior.
     * It inherits from the `Screen` base class, utilizing the specified `AlignmentMode` to
     * dictate how the screen should align its contents.
     *
     * @property mode The alignment mode used to determine the behavior of the visual layout.
     */
    data class QuickVisual(val mode: AlignmentMode) : Screen()
    /**
     * A data class that represents a report within a user interface. The `Report` class
     * extends the `Screen` class and incorporates an `AlignmentMode` to indicate
     * the alignment settings being used in the context of the report.
     *
     * @constructor Initializes a `Report` instance with the provided alignment mode.
     *
     * @property mode The alignment mode to be used for the report. This property determines
     * how elements within the report are aligned.
     */
    data class Report(val mode: AlignmentMode) : Screen()

    data class MSA(val mode: AlignmentMode) : Screen()
}


