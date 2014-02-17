package cdmuhlb.mshexplorer;

final class MshColorspace {
  // CIE XYZ normalization for 6504 K
  private static final double xn = 0.95047;
  private static final double yn = 1.0;
  private static final double zn = 1.0883;

  private static final double finv(final double t) {
    return (t > 6.0/29.0) ? (t*t*t) : ((108.0/841.0)*(t - 4.0/29.0));
  }

  public static final int mshToSRgbArgb(final double m, final double s, final double h) {
    final double labL = m*Math.cos(s);
    final double mss = m*Math.sin(s);
    final double labA = mss*Math.cos(h);
    final double labB = mss*Math.sin(h);

    final double c = (labL + 16.0)/116.0;
    final double x = xn*finv(c + labA/500.0);
    final double y = yn*finv(c);
    final double z = zn*finv(c - labB/200.0);

    final double rLin =  3.2406*x - 1.5372*y - 0.4986*z;
    final double gLin = -0.9689*x + 1.8758*y + 0.0415*z;
    final double bLin =  0.0557*x - 0.2040*y + 1.0570*z;
    int r = (int)Math.rint(255.0*SRgbUtils.forwardTransfer(rLin));
    int g = (int)Math.rint(255.0*SRgbUtils.forwardTransfer(gLin));
    int b = (int)Math.rint(255.0*SRgbUtils.forwardTransfer(bLin));

    boolean outOfGamut = false;
    if (r < 0) { r = 0; outOfGamut = true; }
    else if (r > 255) { r = 255; outOfGamut = true; }
    if (g < 0) { g = 0; outOfGamut = true; }
    else if (g > 255) { g = 255; outOfGamut = true; }
    if (b < 0) { b = 0; outOfGamut = true; }
    else if (b > 255) { b = 255; outOfGamut = true; }

    final int alpha = outOfGamut ? (0xC0<<24) : (0xff<<24);
    return alpha | (r << 16) | (g << 8) | b;
  }

  public static final int mshLToSRgbGrey(final double m, final double s, final double h) {
    final double labL = m*Math.cos(s);
    final double c = (labL + 16.0)/116.0;
    final double y = yn*finv(c);
    final int rgb = (int)Math.rint(255.0*SRgbUtils.forwardTransfer(y));
    return (0xff << 24) | (rgb << 16) | (rgb << 8) | rgb;
  }
}
