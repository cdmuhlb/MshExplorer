package cdmuhlb.mshexplorer;

public final class SRgbUtils {
  public static final double forwardTransfer(final double v) {
    if (v <= 0.0031308) return 12.92*v;
    else return 1.055*Math.pow(v, 1.0/2.4) - 0.055;
  }

  public static final double reverseTransfer(final double v) {
    if (v <= 0.04045) return v/12.92;
    else return Math.pow((v + 0.055)/1.055, 2.4);
  }
}
