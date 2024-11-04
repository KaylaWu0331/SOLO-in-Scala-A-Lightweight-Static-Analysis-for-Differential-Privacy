import sealedTree.{STree, SBranch, SLeaf}
object MatchTreeADTSealed extends App{
  val sealedSeq: Seq[STree[Int]] = Seq(SLeaf(0), SBranch(SLeaf(6), SLeaf(7)))
  val tree2 = for t <- sealedSeq yield t match
    case SBranch(l,r) => (l,r)
    case SLeaf(v) => v
  println(tree2)
}
