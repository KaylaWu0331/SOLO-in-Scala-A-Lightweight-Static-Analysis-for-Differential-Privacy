

sealed trait STree[T]
case class SBranch[T](l:STree[T], r:STree[T]) extends STree[T]
case class SLeaf[T](ele:T) extends STree[T]

