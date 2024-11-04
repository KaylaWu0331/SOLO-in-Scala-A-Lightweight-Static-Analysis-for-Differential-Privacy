package typeclass
import scala.annotation.targetName
trait Semigroup[T]:
  // to generalize the notion of ADDITION(for numbers) or COMPOSITION (for strings))
  extension (t: T)
    infix def combine(other: T): T // an instance extension methode combine
    @targetName("plus") def <+>(other: T): T = t.combine(other) // an alternative operator method <+> which calls combine
trait Monoid[T] extends Semigroup[T]:
  // to add an unit value(e.g., zero or an empty string)
  def unit: T // not defined as an extension method, but as a TYPE-LEVEL or object method
// cuz we only need one instance of the value for all instances of a particular type T

// // comment StringMonoid when using alias given StringMonoid2
//given StringMonoid: Monoid[String] with
//  def unit: String = ""
//  extension (s: String) infix def combine(other: String): String = s + other

given IntMonoid: Monoid[Int] with
  def unit: Int = 0
  extension (i: Int) infix def combine(other: Int): Int = i + other

// // comment NumericMonoid when using alias given
//given NumericMonoid[T: Numeric]: Monoid[T] with
//  def unit:T = summon[Numeric[T]].zero // summon: like the IMPLICITLY method in scala, given type params and returns
//  extension (t:T)
//    infix def combine(other:T): T = summon[Numeric[T]].plus(t, other)

/* Alternative: using clause
 given NumericMonoid[T](using num: Numeric[T]): Monoid[T] with
  def unit:T = num.zero
  extension (t: T)
    infix def combine(other:T):T = num.plus(t,other)*/

/* P150
* - NumericMonoid is a class while String-/IntMonoid is an object, which depends on whether T is specific or not. */

/* Alias given P151
* - keypoint: the body of an alias given instance is = new Monoid[...]: ... , instead of with...
* which creates an anonymous subtype of the Monoid trait
* - NumericMonoid2: a method, called every single time the <+> extension method is used for Numeric[T] value
* - StringMonoid2: a lazy value, only initialized once until the first time we use it and only once, hence delayed initialization */
given NumericMonoid2[T:Numeric]: Monoid[T] = new Monoid[T]:
  println("initializing NumericMonoid2, an alias given instance, with a new subtype of Monoid trait.")
  def unit: T = summon[Numeric[T]].zero
  extension (t:T) infix def combine(other:T):T = summon[Numeric[T]].plus(t,other)
given StringMonoid2: Monoid[String] = new Monoid[String]:
  println("initializing StringMonoid2, an alias given instance, with a new subtype of Monoid trait.")
  def unit = ""
  extension (t:String) infix def combine(other:String):String = t + other

  