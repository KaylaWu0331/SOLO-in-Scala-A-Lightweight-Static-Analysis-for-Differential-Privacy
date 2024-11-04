object MatchRepeatedParams extends App{
  def matchThree(seq: Seq[Int]) = seq match
    case Seq(h1,h2,rest*) => println(s"head 1 = $h1, head 2 = $h2, the rest = $rest")
    case _ => println(s"Other! $seq")

  matchThree(Seq(1, 2, 3, 4))
  matchThree(Seq(1, 2, 3))
  matchThree(Seq(1, 2))
  matchThree(Seq(1))
}
// head 1 = 1, head 2 = 2, the rest = List(3, 4)
//head 1 = 1, head 2 = 2, the rest = List(3)
//head 1 = 1, head 2 = 2, the rest = List()
//Other! List(1)
