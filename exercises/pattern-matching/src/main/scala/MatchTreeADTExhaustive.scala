import treeEnum.Tree
import Tree.{Branch, Leaf}
object MatchTreeADTExhaustive extends App{
  val enumSeq: Seq[Tree[Int]] = Seq(Leaf(0), Branch(Leaf(6), Leaf(7)))
  val tree1 = for t <-enumSeq yield t match
    case Branch(l,r) => (l, r)
    case Leaf(v) => v
  println(tree1)
}
// List(0, (Leaf(6),Leaf(7)))