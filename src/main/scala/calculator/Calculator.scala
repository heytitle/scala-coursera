package calculator

sealed abstract class Expr
final case class Literal(v: Double) extends Expr
final case class Ref(name: String) extends Expr
final case class Plus(a: Expr, b: Expr) extends Expr
final case class Minus(a: Expr, b: Expr) extends Expr
final case class Times(a: Expr, b: Expr) extends Expr
final case class Divide(a: Expr, b: Expr) extends Expr

object Calculator {
  def computeValues(
      namedExpressions: Map[String, Signal[Expr]]): Map[String, Signal[Double]] = {

        val refs = namedExpressions;

        val res = for( (k,v) <- namedExpressions ) yield {
          val sig = Var(0.0);
          sig() = eval( v(), refs );
          ( k, sig );
        }
        res.toMap
  }

  def eval(expr: Expr, references: Map[String, Signal[Expr]]): Double = {
      def value( expr2: Expr ): Double = {
        eval( expr2 , references )
      }

      expr match {
        case Literal(v) => v
        case Ref(s) => { 
          var ref = getReferenceExpr(s, references );
          eval( ref, references - s )
        }
        case Plus(a,b) => value(a) + value(b)
        case Minus(a,b) => value(a) - value(b)
        case Times(a,b) => value(a) * value(b)
        case Divide(a,b) =>value(a) / value(b)
      }
    }

  /** Get the Expr for a referenced variables.
   *  If the variable is not known, returns a literal NaN.
   */
  private def getReferenceExpr(name: String,
      references: Map[String, Signal[Expr]]) = {
    references.get(name).fold[Expr] {
      Literal(Double.NaN)
    } { exprSignal =>
      exprSignal()
    }
  }
}
