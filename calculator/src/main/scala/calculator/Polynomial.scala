package calculator

object Polynomial {
  def computeDelta(a: Signal[Double], b: Signal[Double],
      c: Signal[Double]): Signal[Double] = {
        val delta = Var(0.0);

        delta() = math.pow(b(),2) - 4*a()*c()

        delta;
  }

  def computeSolutions(a: Signal[Double], b: Signal[Double],
      c: Signal[Double], delta: Signal[Double]): Signal[Set[Double]] = {
        val solution = Var( Set(0.0,0.0) );

        val delta   =  computeDelta(a,b,c);

        solution() = delta() match {
          case x if x >= 0 => {
            val adj     = math.sqrt(delta()) / ( 2* a() );
            val first  = ( - b() / (2*a()) );
            Set( first - adj, first + adj );
          }
          case _ => Set()
        }

        solution;

  }
}
