import com.edu.nwalgo.algo.needlemanWunsch
import com.edu.nwalgo.graphics.elements.renderMatrixToImage
import java.io.File
import javax.imageio.ImageIO

fun main(){


    val seq1 = "GATTACATERERERERERERERERERERERERERERERERERERERERERERERERERERERERERECECCECEREEC"
    val seq2 = "GATTACATERERERERERERERERDDDDDDTRERERERERERERERERERERERERERERERERERERERERERECECCECEREEC"
    val result = needlemanWunsch(seq1, seq2, 10, -1, -2)
    val matrix = result.scoreMatrix// твоя сгенерированная матрица из алгоритма
    val path = result.path   // твой путь выравнивания из алгоритма

    val image = renderMatrixToImage(seq1, seq2, matrix, path)
    ImageIO.write(image, "png", File("alignment.png"))

}