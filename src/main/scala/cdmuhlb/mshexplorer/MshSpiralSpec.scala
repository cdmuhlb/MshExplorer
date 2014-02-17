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
    h0Property.addListener(listener)
    hRateProperty.addListener(listener)
  }

  def removeListener(listener: InvalidationListener): Unit = {
    mProperty.removeListener(listener)
    s0Property.removeListener(listener)
    h0Property.removeListener(listener)
    hRateProperty.removeListener(listener)
  }

  def mapValue(z: Double): Int = {
    val s = getS0*(1.0 - z)
    val h = if (s == 0.0) 0.0 else if (s >= 0.5*math.Pi) getH0 else {
      (getH0 + getHRate*math.log(math.tan(0.5*getS0))/getS0) -
          getHRate*math.log(math.tan(0.5*s))/getS0
    }
    MshColorspace.mshToSRgbArgb(getM, s, h) | (0xff << 24)
  }
}
