/**
  * [More about Functors]:
    - Functor.map morphs A => B, for all types A and B, to F[A] => F[B] for any category F.
    - a Functor is a mapping between categories. It maps both the objects and the morphisms.
    - Functor has two additional properties that fall out of the general properties and axioms for category theory:
        1. A functor F preserves identity; that is, the identity of the domain maps to the identity of the codomain.
        2. A functor F preserves composition: F(f ◦ g) = F(f) ◦ F(g).

    [More about Monads]:(P404)
    - monads are called endofunctors(internal functors), that transform a category into itself.
    - flatMap: 
        extracts an element of type A from the context on the left and binds it to a new kind of element in a new context instance.
    - unit:
        behaves like an identity
    [By-name Parameters]:
    - indicated by the => symbol before the type of the parameter A. 
    - This means that the parameter a is not evaluated until it is used inside the function. 
    - a is wrapped in a closure that is only evaluated when it is needed, allowing for lazy evaluation.
      */

trait Functor2[F[_]]:
    def map[A, B](x: F[A])(f: A => B): F[B]

object SeqF extends Functor2[Seq]:
    def map[A, B](seq: Seq[A])(f: A => B): Seq[B] = seq map f

object OptionF extends Functor2[Option]:
    def map[A, B](opt: Option[A])(f: A => B): Option[B] = opt map f

trait Monad2[M[_]]:
    def flatMap[A,B](fa: M[A])(f: A => M[B]): M[B]
    def unit[A](a: => A): M[A] // a: a by-name parameter

object SeqM extends Monad2[Seq]:
    def flatMap[A, B](seq: Seq[A])(f: A => Seq[B]): Seq[B] = seq flatMap f
    def unit[A](a: => A): Seq[A] = Seq(a)

object OptionM extends Monad2[Option]:
    def flatMap[A, B](opt: Option[A])(f: A => Option[B]): Option[B] = opt flatMap f
    def unit[A](a: => A): Option[A] = Option(a)
    
object test3 extends App:
    // test for functors:
    val fid: Int => Double = i => 1.5 * i 
    val res1 = OptionF.map(Some(2))(fid)
    val res2 = OptionF.map(Option.empty[Int])(fid)
    println(res1)
    println(res2)
    // check the associativity:
    val f1: Int => Int = _ * 2
    val f2: Int => Int = _ + 3
    val f3: Int => Int = _ * 5
    val l = List(1,2,3,4,5)
    val m12a = SeqF.map(SeqF.map(l)(f1))(f2) 
    val m23a = (seq: Seq[Int]) => SeqF.map(SeqF.map(seq)(f2))(f3) 
    println(SeqF.map(m12a)(f3)) // 1st element: ((1*2)+3)*5 = 25
    println(m23a(SeqF.map(l)(f1))) // 1st element: ((1*2)+3)*5 = 25
    // test for monads:
    val fm: Int => Seq[Int] = i => 0 until 10 by ((math.abs(i) % 10) + 1)