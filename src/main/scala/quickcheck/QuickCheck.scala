package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  lazy val genHeap: Gen[H] = for {
    v <- arbitrary[A]
    h <- oneOf(  const(empty) , genHeap )
  } yield insert(v, h)

  lazy val genList: Gen[List[Int]] = for {
    v <- arbitrary[Int]
    h <- oneOf(  const(List.empty[Int]) , genList )
  } yield h :+ v

  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)
  implicit lazy val arbList: Arbitrary[List[Int]] = Arbitrary(genList)

  property("gen1") = forAll { (h: H) =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  property("meld") = forAll { (h1: H, h2: H ) =>
    val min1 = findMin(h1)
    val min2 = findMin(h2)

    val m  = meld( h1,h2 )
    val minm = findMin(m)

    ( min1 < min2 & minm == min1 ) | ( min1 >= min2 & minm == min2 )
  }

  property("deleteMin") = forAll { (h: H) =>
    def recur(h: H, prev: A ): Boolean = {
      if( isEmpty(h) ){
        true
      }else{
        val curMin = findMin(h);
        ord.lteq( prev, curMin ) & recur( deleteMin(h), curMin )
      }
    }

    recur( deleteMin(h), findMin(h) )
  }

  property("empyHeap") = forAll { ( a: Int  ) =>
    val emptyHeap = empty
    deleteMin( insert(a, emptyHeap) ) == emptyHeap
  }

  property("2 Ints") = forAll { ( a: Int, b: Int  ) => 
    val j = insert( a, insert(b, empty ));
    val m = findMin(j)
    if( a < b ) m == a
    else m == b
  }

  property("somehting") = forAll { ( l : List[Int] ) =>
    val h = l.foldLeft(empty)( (a, b) => insert(b, a) )

    def recur(h: H, res: List[Int] ): List[Int] = {
      if( isEmpty(h) ) res
      else {
        val m = findMin(h);
        recur( deleteMin(h), res :+ m )
      }
    }
    recur(h, Nil) == l.sorted;
  }

  // property("sortedValue") = forAll { (h: H) =>
  //   def recur(h: H, res: List[A] ): List[A] = {
  //     val m = findMin(h);
  //     recur( deleteMin(h), res :+ m )
  //   }


  //   h.sort = recur( h, Nil );
  // }
}
