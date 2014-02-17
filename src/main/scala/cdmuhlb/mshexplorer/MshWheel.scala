package cdmuhlb.mshexplorer

import javafx.beans.{Observable, InvalidationListener}
import javafx.beans.property.{DoubleProperty, SimpleDoubleProperty}
import javafx.scene.canvas.Canvas
import javafx.scene.image.PixelFormat

class MshWheel(width: Double, height: Double, spec: MshSpiralSpec) extends
    Canvas(width, height) {
  val repaintListener = new InvalidationListener {
    override def invalidated(observable: Observable): Unit = drawWheel()
  }

  spec.mProperty.addListener(repaintListener)
  widthProperty.addListener(repaintListener)
  heightProperty.addListener(repaintListener)
  drawWheel()

  private def drawWheel() {
    val cx = 0.5*(getWidth - 1.0)
    val cy = 0.5*(getHeight - 1.0)
    val rMax = 0.5*getWidth.min(getHeight)
    val nCols = getWidth.ceil.toInt
    val nRows = getHeight.ceil.toInt
    val pw = getGraphicsContext2D.getPixelWriter

    val raster = Array.ofDim[Int](nCols*nRows)
    for (i <- 0 until nRows; j <- 0 until nCols) {
      val dx = j - cx
      val dy = i - cy
      val r = math.hypot(dx, dy)
      if (r <= rMax) {
        val phi = math.atan2(dy, dx)
        val s = 0.5*math.Pi*r/rMax
        val h = phi
        raster(i*nCols + j) = MshColorspace.mshToSRgbArgb(spec.getM, s, h)
      }
    }
    pw.setPixels(0, 0, nCols, nRows, PixelFormat.getIntArgbInstance,
        raster, 0, nCols)
  }
}
