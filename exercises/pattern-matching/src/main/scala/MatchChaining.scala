object MatchChaining extends App{
  val results = for i <- Seq(Some(1), None)
  yield i match {
    case Some(v) => v.toString
    case None => ""
  } match {
    case "" => false
    case _ => true
  }
  println(results)
// List(true, false)
}
