/*
14. [Categories]:
  - A category contains 3 entities:
      1. a class consisting of a set of objects.
      2. a class of morphisms, also called arrows.
          morphisms connect objects: for each morphism f, object A is the domain of f and object B is the codomain of f
      3. a binary operation called morphism composition with the property: 
          for f:A->B and g:B->C, the composision g o f: A->C exists.
  - 2 axioms are satisfied by morphism composition:
      1. one and only one identity: Composision with identity: f o ID = ID o f
      2. associativity: for f:A->B, g:B->C, h:C->D, (h o g) o f = h o (g o f)

[Concepts from Category Theory]:
  - functors(monad, arrow)
  - semigroup
  - monoid
  -applicative    
  
15. [Functors]:
    - A Functor for a type provides the ability for its values to be "mapped over"
    i.e. apply a function that transforms inside a value while remembering its shape.
    - F[_]: the type F takes another type as a type argument.
16. [Monads]:
    - the corresponding abstraction for flatMap, 
    flatMap can avoid List[List[_]] by flatten the values in a single list
    - are endofunctors that transform a category into itself.
    - <=> a Functor for a type constructor F[_] with flatMap and pure operations. 
        flatMap, which turns an F[A] into an F[B] when given a function of type A => F[B]
        pure, which creates an F[A] from a single value A.
*/
trait Functor[F[_]]: // A Functor for the type constructor F[_]
    extension [A](x: F[A]) def map[B](f: A => B): F[B] // use extension method to get rid of summon[Function[F]]
    // <=> def map[A, B](x: F[A], f: A => B): F[B]
    // the ability to transform F[A] to F[B] through the application of function f with type A => B"

given Functor[List] with // given instance: a Funcionar for List
    extension [A](lst: List[A]) def map[B](f: A => B): List[B] = lst map f     

def assertFunctor[F[_]: Functor, A, B](expected: F[B], original: F[A], mapping: A => B): Unit = 
    assert(expected == original.map(mapping))

trait Monad[F[_]] extends Functor[F]:
    def pure[A](x:A): F[A] // give the unit value for a monad
    extension [A](x: F[A]) 
        def flatMap[B](f: A => F[B]): F[B]
        def map[B](f: A => B) = x.flatMap(f.andThen(pure))
end Monad

given listMonad: Monad[List] with
    def pure[A](x: A): List[A] = List(x)
    extension [A](xs: List[A])
        def flatMap[B](f: A => List[B]): List[B] =
            xs.flatMap(f)

object test2 extends App:
    assertFunctor(List("a1", "b1"), List("a", "b"), elt => s"${elt}1")  
    val ori_list = List(1, 2, 3)
    val r =  scala.util.Random
    val expected = ori_list.map(elt => elt + r.nextInt(100))
    println(expected)