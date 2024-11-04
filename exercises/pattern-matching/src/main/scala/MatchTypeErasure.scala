object MatchTypeErasure extends App{
  val doubleList = Seq(5.5,5.6,5.7)
  val stringList = Seq("a","b")
  // to resolve type erase and discriminate btw Seq[Double] and Seq[String]:
  // to match on the collection first,
  // then use a NESTED MATCH on the HEAD ELEMENT to determine the type.
  def doSeqMatch[T<: Matchable](seq:Seq[T]): String = seq match
    case Nil => ""
    case head +: _ => head match
      case head: Double => "a double seq"
      case head: String => "a string seq"
      case _ => "Not a match"

  val results = Seq(doubleList, stringList).map(seq => doSeqMatch(seq))
  println(results)


}
