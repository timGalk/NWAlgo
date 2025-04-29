package com.edu.nwalgo.graphics.elements

import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.awt.*
import java.awt.geom.Path2D

fun renderMatrixToImage(
    seq1: String,
    seq2: String,
    matrix: Array<IntArray>,
    path: List<Pair<Int, Int>>
): BufferedImage {
    val cellSize = 50
    val labelSpace = 2 * cellSize // space for labels (letters)
    val width = matrix[0].size * cellSize + labelSpace
    val height = matrix.size * cellSize + labelSpace

    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val g = image.createGraphics() as Graphics2D

    // Setting up fonts and rendering
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.font = Font("Arial", Font.PLAIN, 18)
    val letterFont = Font("Arial", Font.BOLD, 20)

    // Filling the background
    g.color = Color(255, 255, 255) // white color
    g.fillRect(0, 0, width, height)

    // Drawing the matrix
    for (i in matrix.indices) {
        for (j in matrix[i].indices) {
            val x = j * cellSize + labelSpace
            val y = i * cellSize + labelSpace

            val isPath = path.contains(i to j)
            g.color = if (isPath) Color(144, 238, 144) else Color(220, 220, 220) // green or gray
            g.fillRect(x, y, cellSize, cellSize)

            g.color = Color.BLACK
            g.drawRect(x, y, cellSize, cellSize)

            val value = matrix[i][j]
            val textX = x + cellSize / 4
            val textY = y + cellSize / 2 + 6
            g.drawString(value.toString(), textX, textY)
        }
    }

    // Drawing letters along the edges
    g.font = letterFont
    // On the left (seq1 vertically)
    for (i in seq1.indices) {
        val x = labelSpace / 2 - 10
        val y = (i + 1) * cellSize + labelSpace + cellSize / 2 - 10
        g.drawString(seq1[i].toString(), x, y)
    }
    // On the top (seq2 horizontally)
    for (j in seq2.indices) {
        val x = (j + 1) * cellSize + labelSpace + cellSize / 2 - 10
        val y = labelSpace / 2
        g.drawString(seq2[j].toString(), x, y)
    }

    // Drawing arrows for the path
    g.color = Color(0, 0, 255) // blue color for arrows
    g.stroke = BasicStroke(2f)
    for (i in 0 until path.size - 1) {
        val (fromI, fromJ) = path[i]
        val (toI, toJ) = path[i + 1]

        // Center of the starting and ending cells
        val fromX = fromJ * cellSize + labelSpace + cellSize / 2.0
        val fromY = fromI * cellSize + labelSpace + cellSize / 2.0
        val toX = toJ * cellSize + labelSpace + cellSize / 2.0
        val toY = toI * cellSize + labelSpace + cellSize / 2.0

        // Drawing the line
        g.drawLine(fromX.toInt(), fromY.toInt(), toX.toInt(), toY.toInt())

        // Drawing the arrow (triangle at the end)
        val arrowSize = 8.0
        val angle = Math.atan2(toY - fromY, toX - fromX)
        val arrow = Path2D.Double()
        arrow.moveTo(toX, toY)
        arrow.lineTo(
            toX - arrowSize * Math.cos(angle - Math.PI / 6),
            toY - arrowSize * Math.sin(angle - Math.PI / 6)
        )
        arrow.lineTo(
            toX - arrowSize * Math.cos(angle + Math.PI / 6),
            toY - arrowSize * Math.sin(angle + Math.PI / 6)
        )
        arrow.closePath()
        g.fill(arrow)
    }

    g.dispose()
    return image
}