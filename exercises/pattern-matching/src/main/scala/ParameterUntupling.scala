object ParameterUntupling extends App{
  // 3 ways for decomposition (one layer):
  val tuples = Seq((1,2,3), (4,5,6), (7,8,9))
  val counts1 = tuples.map{
    case (a, b, c) => a + b + c // not exhaustive case clause in anonymous function
  }
  println(counts1) // List(6, 15, 24)
  val counts2 = tuples.map{
    (x,y,z) => x + y + z // for scala 3. can drop "case"
  }
  println(counts2)
  val counts3 = tuples.map(_+_+_)
  println(counts3)
  // 1 way for decomposition (multi-layer):
  val tup2 = Seq((1,(2,3)), (4,(5,6)), (7,(8,9)))
  val counts4 = tup2.map{
    case (x,(y,z)) => x + y + z // only case clause works here
  }
  println(counts4)

}
