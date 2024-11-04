object MatchTuple extends App{
  val langs = Seq(
    ("Scala", "Martin", "Odersky"),
    ("Clojure", "Rich", "Hickey"),
    ("Lisp", "John", "McCarthy"))
  val langs2 = EmptyTuple +: ("Indo-European" *: EmptyTuple) +: langs
  val langs3 = Seq("Scala" -> "Odersky", "Clojure" -> "Hickey")
  println(langs3)

  val results = langs.map {
    case ("Scala", _, _) => "Scala"
    case (lang, first, last) => s"$lang, creator $first $last"
  }
  val results2 = langs.map{
    case "Scala" *: first *: last *: EmptyTuple => s"Scala -> $first -> $last"
    case lang *: rest => s"$lang -> $rest"
  }
  val results3 = langs2.map{
    case "Scala" *: first *: last *: EmptyTuple => s"Scala -> $first -> $last"
    case lang *: rest => s"$lang -> $rest"
    case EmptyTuple => EmptyTuple.toString
  }
  val results4 = langs3.map{
    case "Scala" -> _ => "Scala"
    case lang -> last => s"$lang: $last"
  }
  println(results)
  println(results2)
  println(results3)
  println(results4)


}
// List(Scala, Clojure, creator Rich Hickey, Lisp, creator John McCarthy)
// List(Scala -> Martin -> Odersky, Clojure -> (Rich,Hickey), Lisp -> (John,McCarthy))
// List((), Indo-European -> (), Scala -> Martin -> Odersky, Clojure -> (Rich,Hickey), Lisp -> (John,McCarthy))
// List(Scala, Clojure: Hickey)