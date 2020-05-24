package net.ladstatt.sudoku


import org.bytedeco.opencv.opencv_core.Mat


case class InputFrame(nr: Int, framePipeline: FramePipeline)

case class SolutionFrame(solution: SudokuDigitSolution, solutionMat: Mat) {
  def solutionAsString: String = solution.sliding(9, 9).map(new String(_)).mkString("\n")
}

sealed trait SudokuResult {
  def inputFrame: SCandidate
}

/**
  * Represents a valid sudoku detection, possibly no solution.
  *
  * @param inputFrame
  * @param sudokuFrame
  * @param someSolution
  */
case class SSuccess(inputFrame: SCandidate,
                    sudokuFrame: SRectangle,
                    someSolution: Option[SolutionFrame]) extends SudokuResult

case class SFailure(msg: String, inputFrame: SCandidate) extends SudokuResult


