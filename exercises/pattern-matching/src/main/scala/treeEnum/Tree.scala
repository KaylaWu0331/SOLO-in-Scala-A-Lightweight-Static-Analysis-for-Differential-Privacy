
enum Tree[T]{
  case Branch(left: Tree[T], right: Tree[T])
  case Leaf(elem: T)
}