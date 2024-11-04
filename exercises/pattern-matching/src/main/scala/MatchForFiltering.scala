object MatchForFiltering extends App{
  val elems =Seq((1,2), "Hello", (3,4), 1, 2.2, (5,6))
  val what1 = for (case (x,y) <- elems) yield (y,x)
  println(what1)
// matching that isn’t exhaustive functions as a filter
// List((2,1), (4,3), (6,5))

  val seq = Seq(None, Some(1), None, Some(2.2), None, None, Some("three"))
  val filter = for (case Some(x) <- seq) yield x
  println(filter)



}
