import treeEnum.Tree
import Tree.{Branch, Leaf}
object MatchTreeADTDeep extends App {
  val tree1 = Branch(
    Branch(Leaf(1), Leaf(2)),
    Branch(Leaf(3), Branch(Leaf(4), Leaf(5)))
  )
  val tree2 = Branch(Leaf(6), Leaf(7))
  for t <- Seq(tree1, tree2, Leaf(8))
    yield t match
      case Branch(
      l@Branch(_, _),
      r@Branch(rl@Leaf(rli), rr@Branch(_, _))) => println(s"l=$l, r=$r, rl=$rl, rli=$rli, rr=$rr")
      case Branch(l,r) => println(s"other branch($l, $r)")
      case Leaf(x) => println(s"Other leaf($x)")

}
//l=Branch(Leaf(1),Leaf(2)), r=Branch(Leaf(3),Branch(Leaf(4),Leaf(5))), rl=Leaf(3), rli=3, rr=Branch(Leaf(4),Leaf(5))
//other branch(Leaf(6), Leaf(7))
//Other leaf(8)

