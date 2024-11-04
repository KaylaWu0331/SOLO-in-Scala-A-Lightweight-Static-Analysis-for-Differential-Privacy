object MatchVariable2 extends App{
  val seq2 = Seq(1, 2, 3.14, "one", (6, 7))
  val result2 = seq2.map{ x => x match
    case _: Int => s"$x: an int"
    case _ => s"$x: value other than an int"
  }
  println(result2)
}
// List(1: an int, 2: an int, 3.14: value other than an int, one: value other than an int, (6,7): value other than an int)
