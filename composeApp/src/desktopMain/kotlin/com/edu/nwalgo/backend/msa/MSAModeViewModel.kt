package com.edu.nwalgo.backend.msa

import androidx.lifecycle.ViewModel
import com.edu.nwalgo.backend.algo.AlignmentResult
import com.edu.nwalgo.backend.algo.needlemanWunsch
import com.edu.nwalgo.backend.fastaparser.FastaEntry
import com.edu.nwalgo.backend.multipleseqaligner.MultipleAlignmentResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.apache.batik.dom.GenericDOMImplementation
import org.apache.batik.svggen.SVGGraphics2D
import org.w3c.dom.DOMImplementation
import org.w3c.dom.Document
import java.awt.*
import java.io.File
import java.io.FileWriter
import javax.swing.JFrame
import javax.swing.JPanel

class MSAModeViewModel: ViewModel() {

    private val _alignmentResult = MutableStateFlow<MultipleAlignmentResult?>(null)
    val alignmentResult: StateFlow<MultipleAlignmentResult?> = _alignmentResult

    private var match = 1
    private var  mismatch = -1
    private var gap = -1

    fun updateMatchScore(newMatch: Int) {
        match = newMatch
    }
    fun updateMismatchScore(newMismatch: Int) {
        mismatch = newMismatch
    }
    fun updateGapScore(newGap: Int) {
        gap = newGap
    }

    fun alignSequences(sequences: List<String>) {
        if (sequences.size < 2) return

        val result = performProgressiveAlignment(sequences)
        _alignmentResult.value = result
    }

    private fun performProgressiveAlignment(sequences: List<String>): MultipleAlignmentResult {
        // 1. Compute pairwise scores
        val pairwiseScores = Array(sequences.size) { IntArray(sequences.size) }
        val pairwiseAlignments = Array(sequences.size) { arrayOfNulls<AlignmentResult>(sequences.size) }

        for (i in sequences.indices) {
            for (j in i + 1 until sequences.size) {
                val result = needlemanWunsch(sequences[i], sequences[j], match, mismatch, gap)
                pairwiseScores[i][j] = result.score
                pairwiseScores[j][i] = result.score
                pairwiseAlignments[i][j] = result
                pairwiseAlignments[j][i] = result
            }
        }

        // 2. Find center sequence
        val totalScores = pairwiseScores.map { it.sum() }
        val centerIndex = totalScores.indices.minByOrNull { totalScores[it] }!!

        var currentAlignments = mutableListOf(sequences[centerIndex])

        for (i in sequences.indices) {
            if (i == centerIndex) continue
            val result = needlemanWunsch(currentAlignments[0], sequences[i], match, mismatch, gap)
            currentAlignments = mergeAligned(currentAlignments, result.alignedSeq1)
            currentAlignments.add(result.alignedSeq2)
        }

        val gaps = currentAlignments.sumOf { it.count { ch -> ch == '-' } }
        val matches = currentAlignments[0].indices.count { idx ->
            val column = currentAlignments.map { it[idx] }
            column.toSet().size == 1 && column[0] != '-'
        }
        val identity = matches.toDouble() / currentAlignments[0].length * 100
        val score = calculateTotalScore(currentAlignments, match, mismatch, gap)

        return MultipleAlignmentResult(
            alignedSequences = currentAlignments,
            identity = identity,
            gapCount = gaps,
            score = score
        )
    }

    private fun mergeAligned(existing: List<String>, newAlignedCenter: String): MutableList<String> {
        val result = mutableListOf<String>()

        for (seq in existing) {
            val updated = StringBuilder()
            var i = 0
            for (j in newAlignedCenter.indices) {
                if (newAlignedCenter[j] == '-') {
                    updated.append('-')
                } else {
                    updated.append(seq[i])
                    i++
                }
            }
            result.add(updated.toString())
        }

        return result
    }

    private fun calculateTotalScore(
        alignedSequences: List<String>,
        match: Int,
        mismatch: Int,
        gap: Int
    ): Int {
        val n = alignedSequences.size
        val length = alignedSequences[0].length
        var totalScore = 0

        for (col in 0 until length) {
            for (i in 0 until n) {
                for (j in i + 1 until n) {
                    val a = alignedSequences[i][col]
                    val b = alignedSequences[j][col]

                    totalScore += when {
                        a == '-' || b == '-' -> gap
                        a == b -> match
                        else -> mismatch
                    }
                }
            }
        }

        return totalScore
    }

    fun loadFirstFastaSequence(file: File): String {
        val content = file.readText()
        val entries = parseFasta(content)
        if (entries.isEmpty()) throw Exception("No sequences found in file.")
        return entries[0].sequence
    }

    fun parseFasta(content: String): List<FastaEntry> {
        return content.trim().split(">").filter { it.isNotBlank() }.map { entry ->
            val lines = entry.lines().filter { it.isNotBlank() }
            val header = lines.first().trim()
            val sequence = lines.drop(1).joinToString("").replace("\\s".toRegex(), "").uppercase()
            if (sequence.isBlank()) throw IllegalArgumentException("Empty sequence for header: $header")
            FastaEntry(header, sequence)
        }
    }
    fun generateAlignmentSVG(alignment: List<String>, outputDir: String, fileName: String) {
        val cellSize = 40
        val padding = 10
        val fontSize = 18

        val rows = alignment.size
        val cols = alignment.firstOrNull()?.length ?: 0

        val width = cols * cellSize + padding * 2
        val height = rows * cellSize + padding * 2

        // Prepare output directory
        val dir = File(outputDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val filePath = File(dir, fileName)

        // Create SVG document
        val domImpl: DOMImplementation = GenericDOMImplementation.getDOMImplementation()
        val document: Document = domImpl.createDocument(null, "svg", null)
        val svgGenerator = SVGGraphics2D(document)

        svgGenerator.setSVGCanvasSize(Dimension(width, height))
        svgGenerator.font = Font("JetBrains Mono", Font.PLAIN, fontSize)
        val fontMetrics = svgGenerator.fontMetrics

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val char = alignment[row][col]
                val x = padding + col * cellSize
                val y = padding + row * cellSize

                val columnChars = alignment.map { it[col] }

                val color = when {
                    columnChars.all { it == '-' } -> Color.LIGHT_GRAY
                    columnChars.distinct().size == 1 -> Color(180, 238, 180)  // Greenish
                    char == '-' -> Color(220, 220, 220)
                    else -> Color(255, 160, 160)  // Reddish
                }

                // Draw background
                svgGenerator.color = color
                svgGenerator.fillRect(x, y, cellSize, cellSize)

                // Draw border
                svgGenerator.color = Color.DARK_GRAY
                svgGenerator.drawRect(x, y, cellSize, cellSize)

                // Draw character
                val textWidth = fontMetrics.stringWidth(char.toString())
                val textHeight = fontMetrics.ascent
                val textX = x + (cellSize - textWidth) / 2
                val textY = y + (cellSize + textHeight) / 2 - 4

                svgGenerator.color = Color.BLACK
                svgGenerator.drawString(char.toString(), textX, textY)
            }
        }

        // Save to file
        FileWriter(filePath).use { writer ->
            svgGenerator.stream(writer, true)
        }

        println("SVG saved to: ${filePath.absolutePath}")
    }

    fun showMSAVisualization(alignedSequences: List<String>) {
        if (alignedSequences.isEmpty()) return // Prevent exception on empty input

        val rows = alignedSequences.size
        val cols = alignedSequences.maxOf { it.length }
        val cellSize = 40

        val frame = JFrame("MSA Visualization")
        frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        frame.setSize(cols * cellSize + 100, rows * cellSize + 100)
        frame.isResizable = false

        frame.contentPane = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                val g2 = g as Graphics2D
                g2.font = Font("JetBrains Mono", Font.PLAIN, 18)

                for (i in 0 until rows) {
                    val seq = alignedSequences[i]
                    for (j in 0 until cols) {
                        val c = if (j < seq.length) seq[j] else ' '
                        // Determine color
                        val isGap = c == '-'
                        val isMatch = !isGap && alignedSequences.all { it.length > j && it[j] == c }
                        g2.color = when {
                            isGap -> Color.LIGHT_GRAY
                            isMatch -> Color(144, 238, 144)
                            else -> Color(255, 182, 193)
                        }
                        val x = j * cellSize + 50
                        val y = i * cellSize + 50
                        g2.fillRect(x, y, cellSize, cellSize)
                        g2.color = Color.BLACK
                        g2.drawRect(x, y, cellSize, cellSize)
                        g2.drawString(c.toString(), x + cellSize / 3, y + cellSize / 2 + 10)
                    }
                }
            }
        }
        frame.isVisible = true
    }










}
