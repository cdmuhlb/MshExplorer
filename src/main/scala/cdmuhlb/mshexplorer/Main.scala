package cdmuhlb.mshexplorer

import javafx.application.Application
import javafx.beans.{Observable, InvalidationListener}
import javafx.beans.binding.Bindings
import javafx.beans.property.{DoubleProperty, SimpleDoubleProperty}
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.{Group, Scene}
import javafx.scene.canvas.{Canvas, GraphicsContext}
import javafx.scene.control.Slider
import javafx.scene.image.PixelFormat
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{BorderPane, StackPane, VBox}
import javafx.scene.text.Text
import javafx.scene.paint.Color
import javafx.stage.Stage

object Main extends App {
  Application.launch(classOf[Main], args: _*)
}

class Main extends Application {
  override def start(primaryStage: Stage): Unit = {
    val spec = new MshSpiralSpec

    val group = new Group
    val wheel = new MshWheel(640, 640, spec)
    group.getChildren.add(wheel)
    val traj = new SpiralTrajectory(511, spec)
    traj.widthProperty.bind(wheel.widthProperty)
    traj.heightProperty.bind(wheel.heightProperty)
    group.getChildren.add(traj)

    val txtBox = new VBox
    txtBox.setMouseTransparent(true)
    val mTxt = new Text
    mTxt.textProperty.bind(Bindings.concat("M = ",
        spec.mProperty.asString("%.1f")))
    mTxt.getStyleClass.add("spec-txt")
    txtBox.getChildren.add(mTxt)
    val s0Txt = new Text
    s0Txt.textProperty.bind(Bindings.concat("s0 = ",
        spec.s0Property.asString("%.3g")))
    s0Txt.getStyleClass.add("spec-txt")
    txtBox.getChildren.add(s0Txt)
    val sfTxt = new Text
    sfTxt.textProperty.bind(Bindings.concat("sf = ",
        spec.sfProperty.asString("%.3g")))
    sfTxt.getStyleClass.add("spec-txt")
    txtBox.getChildren.add(sfTxt)
    val h0Txt = new Text
    h0Txt.textProperty.bind(Bindings.concat("h0 = ",
        spec.h0Property.asString("%.3g")))
    h0Txt.getStyleClass.add("spec-txt")
    txtBox.getChildren.add(h0Txt)
    val hRateTxt = new Text
    hRateTxt.textProperty.bind(Bindings.concat("hRate = ",
        spec.hRateProperty.asString("%.3g")))
    hRateTxt.getStyleClass.add("spec-txt")
    txtBox.getChildren.add(hRateTxt)
    val pane = new StackPane
    pane.setAlignment(Pos.TOP_LEFT)
    pane.getChildren.add(group)
    pane.getChildren.add(txtBox)

    val root = new BorderPane
    root.getStyleClass.add("wheel-panel")
    root.setCenter(pane)

    val cb = new MshColorbar(spec)
    cb.setWidth(48)
    cb.heightProperty.bind(wheel.heightProperty)
    root.setRight(cb)

    val vbox = new VBox
    val data1 = new DataExample(spec, DataArray.testPattern1(320, 320))
    vbox.getChildren.add(data1)
    val data2 = new DataExample(spec, DataArray.fromImage(
        getClass.getResource("LightThroughLeaves.png")))
    vbox.getChildren.add(data2)
    root.setLeft(vbox)

    val slider = new Slider(0, 100, 80)
    spec.mProperty.bind(slider.valueProperty)
    root.setBottom(slider)

    val scene = new Scene(root)
    scene.getStylesheets.add(getClass.getResource("Main.css").toExternalForm)
    primaryStage.setTitle("Msh Explorer")
    primaryStage.setScene(scene)
    primaryStage.show()
  }
}
