package scalashop

import org.scalameter._
import taskSchedule._

object VerticalBoxBlurRunner {

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 5,
    Key.exec.maxWarmupRuns -> 10,
    Key.exec.benchRuns -> 10,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val radius = 3
    val width = 1920
    val height = 1080
    val src = new Img(width, height)
    val dst = new Img(width, height)
    val seqtime = standardConfig measure {
      VerticalBoxBlur.blur(src, dst, 0, width, radius)
    }
    println(s"sequential blur time: $seqtime ms")

    val numTasks = 32
    val partime = standardConfig measure {
      VerticalBoxBlur.parBlur(src, dst, numTasks, radius)
    }
    println(s"fork/join blur time: $partime ms")
    println(s"speedup: ${seqtime / partime}")
  }

}

/** A simple, trivially parallelizable computation. */
object VerticalBoxBlur {

  def blur(src: Img, dst: Img, from: Int, end: Int, radius: Int): Unit = {
    for( i <- from until end; j <- 0 until src.height ){
       dst(i,j) = boxBlurKernel( src, i, j, radius );
    }
  }

  /** Blurs the columns of the source image in parallel using `numTasks` tasks.
   *
   *  Parallelization is done by stripping the source image `src` into
   *  `numTasks` separate strips, where each strip is composed of some number of
   *  columns.
   */
  def parBlur(src: Img, dst: Img, numTasks: Int, radius: Int): Unit = {
    val colsPerTask = if( src.width / numTasks == 0 ) 1 else src.width / numTasks;

    val futures = List();

    val starts = ( 0 to src.width by colsPerTask ).toList;
    val ends   = starts.tail :+ (src.width -1);
    val strips = starts.zip( ends );

    strips.map(
      p => task(blur( src, dst, p._1, p._2, radius ))
    ).map( _.join );
  }

}
