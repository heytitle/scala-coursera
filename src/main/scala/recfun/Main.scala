package recfun
import common._

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
        println()
    }
  }

  /**
   * Exercise 1
   * TODO: Tail recursive
   */
  def pascal(c: Int, r: Int): Int = {
    if( c == r || c <= 0 || r <= 1 ) {
      1
    }else{
      pascal(c-1, r-1) + pascal( c, r-1 )
    }
  }

  /**
   * Exercise 2
   */
  def balance(chars: List[Char]): Boolean = {

    def balanceIter( chars: List[Char], par: Int ): Boolean = {
      if( chars.isEmpty ) {
        if (par == 0) {
          true
        } else {
          false
        }
        } else if( chars.head == '(' ) {
          balanceIter( chars.tail, par + 1 )
        } else if ( chars.head == ')' ) {
          if( par >= 1 ){
            balanceIter( chars.tail, par - 1 )
          }else{
            false
          }
          } else {
            balanceIter( chars.tail, par )
          }
    }
    balanceIter( chars, 0 )
  }

  /**
   * Exercise 3
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    var way = 0;
    def countIter( money_left: Int, coins: List[Int] ): Unit = {
      var bucket = coins;
      if( money_left == 0 ){
        way = way + 1;
      }else {
        /* For loop */
       while (!bucket.isEmpty) {
         var money = money_left - bucket.head;
         if( money >= 0 ){
           countIter( money , bucket );
         }
         bucket = bucket.tail;
       }
      }
    }
    countIter( money, coins );
    way;
  }
  /* http://www.billthelizard.com/2010/12/sicp-219-counting-change-revisited.html */
  // def countChange(money: Int, coins: List[Int]): Int = {
  //   if( money == 0 ) {
  //     1;
  //   } else if(money > 0 && !coins.isEmpty) {
  //     countChange(money - coins.head, coins) + countChange(money, coins.tail);
  //   } else { 0 }
  // }
}
