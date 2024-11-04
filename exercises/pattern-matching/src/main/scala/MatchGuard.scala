object MatchGuard extends App{
  val results = Seq(1,2,3,4).map{
    case e if e%2 == 0 => s"even: $e"
    case o => s"odd:$o"
  }
  println(results)

}
