package cdmuhlb.mshexplorer

import java.net.URL
import javax.imageio.ImageIO
import javafx.beans.{Observable, InvalidationListener}
import javafx.beans.property.{DoubleProperty, SimpleDoubleProperty}
import javafx.event.EventHandler
import javafx.scene.{Group, Scene}
import javafx.scene.canvas.{Canvas, GraphicsContext}
import javafx.scene.control.Slider
import javafx.scene.image.PixelFormat
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color

class DataArray(val width: Int, val height: Int) {
  val data = new Array[Double](width*height)
}

object DataArray {
  def testPattern1(width: Int, height: Int): DataArray = {
    val ans = new DataArray(width, height)
    for (i <- 0 until height; j <- 0 until width) {
      val x = 2.0*math.Pi*j.toDouble/(width - 1)
      val y = i.toDouble/(height - 1)
      val z = 0.5*math.sin(x*x)*y + 0.5
      ans.data(i*width + j) = z
    }
    ans
  }

  def testPattern2(width: Int, height: Int): DataArray = {
    val ans = new DataArray(width, height)
    for (i <- 0 until height; j <- 0 until width) {
      val x = j.toDouble/(width - 1)
      val y = i.toDouble/(height - 1)
      val z2 = 0.5*math.sin(16.0*math.Pi*x)*math.sin(8.0*math.Pi*y) + (x - 0.5)
      val z = 0.5*z2 + 0.5
      ans.data(i*width + j) = z
    }
    ans
  }

  def fromImage(url: URL): DataArray = {
    val img = ImageIO.read(url)
    val argb = new Array[Int](img.getWidth*img.getHeight)
    img.getRGB(0, 0, img.getWidth, img.getHeight, argb, 0, img.getWidth)
    val ans = new DataArray(img.getWidth, img.getHeight)

    for (i <- 0 until argb.length) {
      val c = argb(i)
      val bLin = SRgbUtils.reverseTransfer((c & 0xff) / 255.0f);
      val gLin = SRgbUtils.reverseTransfer(((c >>> 8) & 0xff) / 255.0f);
      val rLin = SRgbUtils.reverseTransfer(((c >>> 16) & 0xff) / 255.0f);
      val y = 0.2126*rLin + 0.7152*gLin + 0.0722*bLin;
      ans.data(i) = SRgbUtils.forwardTransfer(y)
    }
    ans
  }
}

class DataExample(spec: MshSpiralSpec, data: DataArray) extends
    Canvas(data.width, data.height) {
  val repaintListener = new InvalidationListener {
    override def invalidated(observable: Observable): Unit = drawData()
  }

  spec.addListener(repaintListener)
  widthProperty.addListener(repaintListener)
  heightProperty.addListener(repaintListener)
  drawData()

  private def drawData() {
    val nCols = getWidth.ceil.toInt
    val nRows = getHeight.ceil.toInt

    val raster = new Array[Int](nCols*nRows)
    for (i <- 0 until nRows; j <- 0 until nCols) {
      val ii = math.rint(i*(data.height - 1) / (nRows - 1)).toInt
      val jj = math.rint(j*(data.width - 1) / (nCols - 1)).toInt
      val z = data.data(ii*data.width + jj)
      raster(i*nCols + j) = spec.mapValue(z)
    }
    val pw = getGraphicsContext2D.getPixelWriter
    pw.setPixels(0, 0, nCols, nRows, PixelFormat.getIntArgbPreInstance,
        raster, 0, nCols)
  }
}
