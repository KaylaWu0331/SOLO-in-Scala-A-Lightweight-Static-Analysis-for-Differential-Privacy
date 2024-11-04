object MatchVariable extends App{
  // an example of Pattern Matching that matches on:
  // - specific values
  // - all values of specific types
  // - a default clause that matches anything: other
  val seq = Seq(1, 2, 3.14, 5.5F, "one", "four", true, (6, 7))
  val result = seq.map{
    case 1 => "an int 1"
    case i:Int => s"$i: an int other than 1"
    case d:(Double|Float) => s"$d: a double or float"
    case "one" => "a string one"
    case s: String => s"$s: a string other than one"
    case (a,b) => s"($a,$b): a tuple"
    case other => s"$other: an unmatched member"
  }
  println(result)
}
//List(an int 1, 2: an int other than 1, 3.14: a double or float, 5.5: a double or float, a string one, four: a string other than one, true: an unmatched member, (6,7): a tuple)