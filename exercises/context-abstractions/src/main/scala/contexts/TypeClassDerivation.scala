package contexts
import scala.language.strictEquality
// when we import strictEquality, the comparison operators == and != are only allowed in certain specific contexts
enum Tree[T] derives CanEqual:
  /* With derives CanEqual clause, we can compare Tree[T] instances for the same T types
  * - Tree: deriving type
   - CanEqual instance: derived instance
  *  */
  case Branch(left:Tree[T], right: Tree[T])
  case Leaf(elem:T)

@main def TryDrived() =
  import Tree.*
  val l1 = Leaf("l1") // contexts.Tree[String]
  val l2 = Leaf(2) // contexts.Tree[Int]
  val b = Branch(l1, Branch(Leaf("b1"), Leaf("b2"))) // contexts.Tree[String]
  println(l1 == l1)
//  println(l1 != l2)
  println(l1 != b)
  println(s"For Int, Int: ${summon[CanEqual[Tree[Int],Tree[Int]]]}")
// For Int, Int: scala.CanEqual$derived$@5ebec15
  /* The derives CanEqual clause has the effect of generating the given instance */