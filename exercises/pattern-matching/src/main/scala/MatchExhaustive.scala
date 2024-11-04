object MatchExhaustive extends App{
  val seq3 = Seq(Some(1), None, Some(2), None)
  val result3 = seq3.map{
    case Some(i) => s"int $i"
    case None => ""
  }
  println(result3)
}
// List(int 1, , int 2, )