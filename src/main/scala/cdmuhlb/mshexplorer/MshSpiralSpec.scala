package cdmuhlb.mshexplorer

import javafx.beans.InvalidationListener
import javafx.beans.property.{DoubleProperty, SimpleDoubleProperty}

class MshSpiralSpec {
  private val m = new SimpleDoubleProperty(80)
  final def getM: Double = m.get
  final def setM(value: Double): Unit = m.set(value)
  def mProperty: DoubleProperty = m

  private val s0 = new SimpleDoubleProperty(0.25*math.Pi)
  final def getS0: Double = s0.get
  final def setS0(value: Double): Unit = s0.set(value)
  def s0Property: DoubleProperty = s0

  private val sf = new SimpleDoubleProperty(0.1)
  final def getSf: Double = sf.get
  final def setSf(value: Double): Unit = sf.set(value)
  def sfProperty: DoubleProperty = sf

  private val h0 = new SimpleDoubleProperty(0.0)
  final def getH0: Double = h0.get
  final def setH0(value: Double): Unit = h0.set(value)
  def h0Property: DoubleProperty = h0

  private val hRate = new SimpleDoubleProperty(2.0)
  final def getHRate: Double = hRate.get
  final def setHRate(value: Double): Unit = hRate.set(value)
  def hRateProperty: DoubleProperty = hRate

  def addListener(listener: InvalidationListener): Unit = {
    mProperty.addListener(listener)
    s0Property.addListener(listener)
    sfProperty.addListener(listener)
    h0Property.addListener(listener)
    hRateProperty.addListener(listener)
  }

  def removeListener(listener: InvalidationListener): Unit = {
    mProperty.removeListener(listener)
    s0Property.removeListener(listener)
    sfProperty.removeListener(listener)
    h0Property.removeListener(listener)
    hRateProperty.removeListener(listener)
  }

  def mapValue(z: Double): Int = {
    val (s, h) = mapToSH(z)
    MshColorspace.mshToSRgbArgb(getM, s, h) | (0xff << 24)
  }

  def mapToSH(z: Double): (Double, Double) = {
    val s = (getSf - getS0)*z + getS0
    val h = if (s <= 0.0) 0.0 else if (s >= 0.5*math.Pi) getH0 else {
      (getH0 + getHRate*math.log(math.tan(0.5*getS0))/(getSf - getS0)) -
          getHRate*math.log(math.tan(0.5*s))/(getSf - getS0)
    }
    (s, h)
  }
}
