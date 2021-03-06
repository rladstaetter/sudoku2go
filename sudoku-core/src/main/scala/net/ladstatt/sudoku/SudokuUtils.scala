package net.ladstatt.sudoku

import java.nio.file.Path

import net.ladstatt.sudoku.Parameters._
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.opencv_core._

import scala.concurrent.duration.FiniteDuration

case class PFloat(x: Float, y: Float) {
  val dist: Double = Math.sqrt(x.toDouble * x.toDouble + y.toDouble * y.toDouble)
}

case class PInt(x: Int, y: Int) {
  val dist: Double = Math.sqrt(x.toDouble * x.toDouble + y.toDouble * y.toDouble)
}

/**
 * Contains most of the algorithms necessary for the SudokuFX application.
 */
object SudokuUtils {

  def solve(solutionCandidate: Seq[Int]
            , maxDuration: FiniteDuration): Option[SudokuState] = {
    BruteForceSolver.solve(solutionCandidate, maxDuration).map(solution => SudokuState(solution, Sudoku.minNrOfValueHits))
  }

  def toSolutionCells(persistData: Boolean
                      , frameNr: Int
                      , digitLibrary: DigitLibrary
                      , sudokuHistory: SudokuState
                      , targetPath: Path): Seq[SCell] = {
    (for (pos <- cellRange) yield {
      val value = sudokuHistory.cellValues(pos)

      val x: Option[SCell] =
        if (value != 0) {
          val someM: Option[Mat] = digitLibrary.digits(value).optMat
          (if (someM.isEmpty) {
            digitLibrary.digits(value).optMat
          } else someM)
            .map(m => SCell(persistData, "fixmeid", frameNr, pos, m, new Rect, sudokuHistory.hitHistory(pos), targetPath))
        } else None
      x
    }).flatten
  }


  /**
   * provides a fallback if there is no digit detected for this number.
   *
   * the size and type of the mat is calculated by looking at the other elements of the digit
   * library. if none found there, just returns None
   *
   * @param number
   * @return
   */
  def mkFallback(number: Int, digitLibrary: DigitLibrary): Option[Mat] = {
    /**
     * returns size and type of Mat's contained in digitLibrary
     *
     * @return
     */
    def determineMatParams(): Option[(Size, Int)] = {
      digitLibrary.digits.values.flatMap(_.optMat).headOption.map(m => (m.size, m.`type`))
    }

    for ((size, matType) <- determineMatParams()) yield {
      val m = new Mat(new Scalar(new Scalar(255, 255, 255, 255)))
      val mat = new Mat(size.height, size.width, matType).setTo(m)
      opencv_imgproc.putText(mat, number.toString, new Point((size.width * 0.3).toInt, (size.height * 0.9).toInt), opencv_imgproc.FONT_HERSHEY_TRIPLEX, 2, new Scalar(0, 0, 0, 255))
      mat
    }
  }


}



