# Animated Matrix - Needleman-Wunsch Alignment Tool

## Overview

This project is a Kotlin-based desktop application that visualizes the Needleman-Wunsch algorithm for sequence alignment. It provides an interactive interface and allows exporting alignment reports to PDF.

## Features

- **Interactive UI**: Built with Jetpack Compose for Desktop.
- **Sequence Alignment**: Implements the Needleman-Wunsch algorithm.
- **PDF Export**: Generate detailed alignment reports in PDF format (saves data in the folder of progrmm 'results').*Availible in **report** mode*
- **PNG Export**: Generate detailed
- **Cross-Platform**: Supports Windows, macOS, and Linux.

## Requirements

- **Kotlin**: Multiplatform setup with JVM target.
- **Gradle**: Build system with Kotlin DSL.
- **Dependencies**:
    - Jetpack Compose for Desktop
    - iText

## Instalation
Download **MSI** file from [releases](https://github.com/timGalk/NWAlgo/releases)
Run a programm as **admin**


<img src="https://github.com/user-attachments/assets/f67122ec-ac75-48ac-a1a5-173aa4bc5493" width="300" /> <img src="https://github.com/user-attachments/assets/77bdc836-8dbf-4048-bac6-d9cb6adf3425" width="300" />


## Build and Run
### Prerequisites

1. Install [JDK 17+](https://adoptopenjdk.net/).
2. Install [Gradle](https://gradle.org/).

### Steps

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd composeApp
    ```
2. Open the project in your favorite IDE (e.g., IntelliJ IDEA).
3. Build the project:
   ```bash
   ./gradlew build
   ```

## User guide
1. **Input Sequences**: Enter the sequences you want to align in the provided text fields.
2. **Set Parameters**: Choose the scoring matrix and gap penalties.
3. **Run Alignment**: Click the "Align" button to start the alignment process.
4. **View Results**: The results will be displayed in the UI, showing the aligned sequences and the alignment score.

### Fast mode
The lenght of sequences is limited to 20 characters.There aren't oppurtunity to have a detailed report.
### Report mode
The lenght of sequences is unlimited. There are oppurtunity to have a detailed report in PDF format and PNG image. **Warning:** The report will be saved in the folder of program 'results'. Launch the program as admin to have a permission to save the report in this folder. The report will be saved in the folder of program 'results'. Launch the program as admin to have a permission to save the report in this folder.


