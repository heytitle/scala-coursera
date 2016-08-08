
import common._


package object scalashop {

  /** The value of every pixel is represented as a 32 bit integer. */
  type RGBA = Int

  /** Returns the red component. */
  def red(c: RGBA): Int = (0xff000000 & c) >>> 24

  /** Returns the green component. */
  def green(c: RGBA): Int = (0x00ff0000 & c) >>> 16

  /** Returns the blue component. */
  def blue(c: RGBA): Int = (0x0000ff00 & c) >>> 8

  /** Returns the alpha component. */
  def alpha(c: RGBA): Int = (0x000000ff & c) >>> 0

  /** Used to create an RGBA value from separate components. */
  def rgba(r: Int, g: Int, b: Int, a: Int): RGBA = {
    (r << 24) | (g << 16) | (b << 8) | (a << 0)
  }

  /** Restricts the integer into the specified range. */
  def clamp(v: Int, min: Int, max: Int): Int = {
    if (v < min) min
    else if (v > max) max
    else v
  }

  /** Image is a two-dimensional matrix of pixel values. */
  class Img(val width: Int, val height: Int, private val data: Array[RGBA]) {
    def this(w: Int, h: Int) = this(w, h, new Array(w * h))
    def apply(x: Int, y: Int): RGBA = data(y * width + x)
    def update(x: Int, y: Int, c: RGBA): Unit = data(y * width + x) = c
    override def toString() : String = { 
      data.mkString(" ")
    }
  }

  /** Computes the blurred RGBA value of a single pixel of the input image. */
  def boxBlurKernel(src: Img, x: Int, y: Int, radius: Int): RGBA = {
    var res : List[Int]= List(0,0,0,0);
    var count = 0;
    if( radius > 0  ){
      for (
           i <- -radius until radius + 1;
           j <- -radius until radius + 1
          if
              0 <= i + x
              & i + x < src.width
              & 0 <= j + y
              & j + y < src.height
          ) {
          var pixel = src(
            i + x,
            j + y
          )
          res = List(
            res(0) + red(pixel),
            res(1) + green(pixel),
            res(2) + blue(pixel),
            res(3) + alpha(pixel)
          )
          count = count + 1;
      }
      res = res.map( _ / count );
      rgba( res(0), res(1), res(2), res(3) );
    }else {
      src(x,y)
    }
  }

}
