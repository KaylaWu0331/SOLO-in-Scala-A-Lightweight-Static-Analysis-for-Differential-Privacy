// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html
import munit.ScalaCheckSuite
import org.scalacheck.*
import Prop.forAll
import SeqM.*
class MonadPropertiesSuite extends ScalaCheckSuite:
    // Arbitrary function:
    val f1: Int => Seq[Int] = i => 0 until 10 by ((math.abs(i) % 10) + 1)
    val unitInt: Int => Seq[Int] = (i:Int) => unit(i)
    val f2: Int => Seq[Int] = i => Seq(i+1)

    property("Monad law for unit works for Sequence Monads") {
        forAll { (i: Int) =>
        val seq: Seq[Int] = Seq(i)
        flatMap(unit(i))(f1) == f1(i) && 
        flatMap(seq)(unitInt) == seq 
        }
    }
    property("Monad law for function composition works for Sequence Monads") {
        forAll { (i: Int) =>
        val seq = Seq(i)
        flatMap(flatMap(seq)(f1))(f2) == 
        flatMap(seq)(x => flatMap(f1(x))(f2))
        }
    }