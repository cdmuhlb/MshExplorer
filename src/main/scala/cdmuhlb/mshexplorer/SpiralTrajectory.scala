package cdmuhlb.mshexplorer

import javafx.beans.{Observable, InvalidationListener}
import javafx.beans.property.{DoubleProperty, SimpleDoubleProperty}
import javafx.event.EventHandler
import javafx.scene.{Cursor, Parent}
import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.{Circle, LineTo, MoveTo, Path}

class SpiralTrajectory(nLines: Int, spec: MshSpiralSpec) extends Parent {
  private val start = new MoveTo
  private val lines = new java.util.ArrayList[LineTo](nLines)
  for (i <- 1 to nLines) lines.add(new LineTo)

  private val width: DoubleProperty = new SimpleDoubleProperty
  final def getWidth: Double = width.get
  final def setWidth(value: Double): Unit = width.set(value)
  def widthProperty: DoubleProperty = width

  private val height: DoubleProperty = new SimpleDoubleProperty
  final def getHeight: Double = height.get
  final def setHeight(value: Double): Unit = height.set(value)
  def heightProperty: DoubleProperty = height

  private val path = new Path
  path.getStyleClass.add("spiral-path")
  path.getElements.add(start)
  path.getElements.addAll(lines)
  getChildren.add(path)

  private val clickPath = new Path
  clickPath.setCursor(Cursor.HAND)
  clickPath.setStroke(Color.TRANSPARENT)
  clickPath.setStrokeWidth(13)
  clickPath.getElements.add(start)
  clickPath.getElements.addAll(lines)
  getChildren.add(clickPath)
  val rateHandler = new EventHandler[MouseEvent] {
    private var originX: Double = 0.5*getWidth
    private var originY: Double = 0.5*getHeight
    private var originPhi: Double = 0.0
    private var originRate: Double = spec.getHRate

    override def handle(event: MouseEvent): Unit = {
      val x = event.getX
      val y = event.getY
      val cx = 0.5*(getWidth - 1.0)
      val cy = 0.5*(getHeight - 1.0)
      event.getEventType match {
        case MouseEvent.MOUSE_PRESSED =>
          originX = x
          originY = y
          originPhi = math.atan2(x - cx, y - cy)
          //originPhi = getH0  // incompatible with ro
          originRate = spec.getHRate
        case MouseEvent.MOUSE_DRAGGED =>
          val rMax = 0.5*getWidth.min(getHeight)
          val dxo = x - originX
          val dyo = y - originY
          val ro = math.hypot(dxo, dyo)
          val phi = math.atan2(x - cx, y - cy)
          val deltaRate = 5.0*ro/rMax*math.sin(originPhi - phi)
          spec.setHRate(originRate + deltaRate)
      }
    }
  }
  clickPath.setOnMousePressed(rateHandler)
  clickPath.setOnMouseDragged(rateHandler)

  private val seed = new Circle(5)
  seed.getStyleClass.add("spiral-circle")
  seed.centerXProperty.bind(start.xProperty)
  seed.centerYProperty.bind(start.yProperty)
  getChildren.add(seed)

  seed.setOnMouseDragged(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = {
      val i = event.getY
      val j = event.getX
      val cx = 0.5*(getWidth - 1.0)
      val cy = 0.5*(getHeight - 1.0)
      val rMax = 0.5*getWidth.min(getHeight)
      val dx = j - cx
      val dy = i - cy
      val r = math.hypot(dx, dy)
      val phi = math.atan2(dy, dx)
      spec.setS0(0.5*math.Pi*r/rMax)
      spec.setH0(phi)
    }
  })

  private val tail = new Circle(5)
  tail.getStyleClass.add("spiral-circle")
  tail.centerXProperty.bind(lines.get(nLines-1).xProperty)
  tail.centerYProperty.bind(lines.get(nLines-1).yProperty)
  getChildren.add(tail)

  tail.setOnMouseDragged(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = {
      val i = event.getY
      val j = event.getX
      val cx = 0.5*(getWidth - 1.0)
      val cy = 0.5*(getHeight - 1.0)
      val rMax = 0.5*getWidth.min(getHeight)
      val dx = j - cx
      val dy = i - cy
      val r = math.hypot(dx, dy)
      spec.setSf(0.5*math.Pi*r/rMax)
    }
  })

  private val updater = new InvalidationListener {
    override def invalidated(observable: Observable): Unit = {
      val cx = 0.5*(getWidth - 1.0)
      val cy = 0.5*(getHeight - 1.0)
      val rMax = 0.5*getWidth.min(getHeight)

      val r0 = (2.0/math.Pi)*rMax*spec.getS0
      start.setX(cx + r0*math.cos(spec.getH0))
      start.setY(cy + r0*math.sin(spec.getH0))

      for (k <- 1 to nLines) {
        val z = k.toDouble / nLines
        val (s, h) = spec.mapToSH(z)
        val r = s*rMax*(2.0/math.Pi)
        val line = lines.get(k - 1)
        line.setX(cx + r*math.cos(h))
        line.setY(cy + r*math.sin(h))
      }
    }
  }

  widthProperty.addListener(updater)
  heightProperty.addListener(updater)
  spec.s0Property.addListener(updater)
  spec.sfProperty.addListener(updater)
  spec.h0Property.addListener(updater)
  spec.hRateProperty.addListener(updater)
}
