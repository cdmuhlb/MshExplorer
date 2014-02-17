package cdmuhlb.mshexplorer

import javafx.beans.{Observable, InvalidationListener}
import javafx.scene.canvas.Canvas
import javafx.scene.image.PixelFormat

class MshColorbar(spec: MshSpiralSpec) extends Canvas {
  val repaintListener = new InvalidationListener {
    override def invalidated(observable: Observable): Unit = drawBar()
  }

  spec.addListener(repaintListener)
  widthProperty.addListener(repaintListener)
  heightProperty.addListener(repaintListener)
  drawBar()

  private def drawBar() {
    val nCols = getWidth.ceil.toInt
    val nRows = getHeight.ceil.toInt
    val pw = getGraphicsContext2D.getPixelWriter

    val raster = Array.ofDim[Int](nCols*nRows)
    for (i <- 0 until nRows) {
      val z = 1.0 - i.toDouble/(nRows - 1)
      for (j <- 0 until nCols) {
        raster(i*nCols + j) = spec.mapValue(z)
      }

      // Ideal (unclipped) greyscale
      /*val grey = MshColorspace.mshLToSRgbGrey(getM, s, h)
      for (j <- nCols/2 until nCols) {
        raster(i*nCols + j) = grey
      }*/
    }
    pw.setPixels(0, 0, nCols, nRows, PixelFormat.getIntArgbPreInstance,
        raster, 0, nCols)
  }
}
