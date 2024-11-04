object MatchSeq extends App{
  // how to iterate through a Seq using pm and recursion
  def seqToString[T](seq: Seq[T]): String = seq match
    case head +: tail => s"($head +: ${seqToString(tail)})"
    case Nil => "Nil"

  println(seqToString(Seq(1,2,3)))
  println(seqToString((Seq.empty[Int])))
  println(seqToString(Vector(1, 2, 3)))
  println(seqToString((Vector.empty[Int])))
  println(seqToString(Map("one" -> 1, "two" -> 2, "three" -> 3).toSeq))
  println(seqToString(Map.empty[String, Int].toSeq))
}
// (1 +: (2 +: (3 +: Nil)))
//Nil
//(1 +: (2 +: (3 +: Nil)))
//Nil
//((one,1) +: ((two,2) +: ((three,3) +: Nil)))
//Nil
