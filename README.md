MshExplorer
===========

Interactively create perceptually uniform colormaps.  This GUI application
allows one to visualize the Msh colorspace and to design perceptually uniform
trajectories with constant M and linear s.  The parameters of these trajectories
can then be used to construct colormaps for data visualization (see the
`MshRainbow` colormap in __DgView__ for an example).

Dependencies
------------

__MshExplorer__ is written in Scala and Java and uses JavaFX.  It therefore
requires a Java SE 7 (or later) JDK and JavaFX 2.2 (`jfxrt.jar` is expected to
live in `$JAVA_HOME/jre/lib`).  Building __MshExplorer__ requires an
[SBT](http://www.scala-sbt.org/) launcher compatible with version 0.13.1.
