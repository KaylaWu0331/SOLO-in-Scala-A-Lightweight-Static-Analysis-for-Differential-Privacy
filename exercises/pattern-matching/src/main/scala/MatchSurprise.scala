object MatchSurprise extends App{
  // to present the lowercase and capital letter in the name of a variable in pattern matching
  def checkYBad(y: Int): Seq[String] =
    for x <- Seq(99,100,101)
      yield x match
        case y => s"found y=$y" // match anything because there is no type declaration,
        // and assign it to this new variable named y.
        // y is not interpreted as a reference to the method parameter y
        case i:Int => s"$i: int"

  println(checkYBad(100))
  println(checkYBad(102))
  //List(found y=99, found y=100, found y=101)
  //List(found y=99, found y=100, found y=101)
  def checkYGood1(Y:Int): Seq[String] =
    for x <- Seq(99, 100, 101)
      yield x match
        case Y => s"found y=$Y"
        case i: Int => s"$i: int"

  println(checkYGood1(100))
  println(checkYGood1(102))
  //List(99: int, found y=100, 101: int)
  //List(99: int, 100: int, 101: int)
  def checkYGood2(y:Int): Seq[String] =
    for x <- Seq(99, 100, 101)
      yield x match
        case `y`=> s"found y=$y"
        case i: Int => s"$i: int"

  println(checkYGood2(100))
  println(checkYGood2(102))
  //List(99: int, found y=100, 101: int)
  //List(99: int, 100: int, 101: int)
}
