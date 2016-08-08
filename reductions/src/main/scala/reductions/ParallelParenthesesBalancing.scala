package reductions

import scala.annotation._
import org.scalameter._
import common._

object ParallelParenthesesBalancingRunner {

  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 120,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val length = 100000000
    val chars = new Array[Char](length)
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime ms")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime ms")
    println(s"speedup: ${seqtime / fjtime}")
  }
}

object ParallelParenthesesBalancing {

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def balance(chars: Array[Char]): Boolean = {
    def iter( chars: Array[Char], count: Int  ) : Int = {
      if( chars.length > 0 ){
        val t = chars(0)
        val tail = chars.drop(1)
        if( t == '(' && count >= 0 ) iter( tail, count + 1 )
        else if ( t == ')' ) iter( tail, count - 1  )
        else iter( tail, count )
      } else {
        count
      }
    }
    iter( chars, 0 ) == 0
  }

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def parBalance(chars: Array[Char], threshold: Int): Boolean = {

    def traverse( idx: Int, until: Int ) : ( Int, Int ) = {
      var args1, args2 :Int = 0;
      var count = 0;
      var i = idx;
      while( i < until ){
        chars(i) match {
          case '(' => {
            args1 = args1 + 1
          }
          case ')' => {
            if( args1 > 0 ){
              args1 = args1 - 1
            } else {
              args2 = args2 + 1
            }
          }
          case _ => Nil
        }
        i = i + 1;
      }
      ( args1, args2 )
    }

    def reduce(from: Int, until: Int) : ( Int, Int ) =  {
      if( until - from <= threshold ){
        traverse( from, until )
      } else {
      val mid = math.floor( ( until + from ) / 2 ).toInt;
        val ( a, b ) = parallel( reduce(from, mid), reduce( mid, until ) )

        if( a._1 > b._2 ){
          ( a._1 + b._1 - b._2, a._2 )
        } else {
          ( b._1, b._2 - a._1 + a._2 )
        }
      }
    }

    val res = reduce(0, chars.length)

    res == (0,0)
  }

  // For those who want more:
  // Prove that your reduction operator is associative!

}
