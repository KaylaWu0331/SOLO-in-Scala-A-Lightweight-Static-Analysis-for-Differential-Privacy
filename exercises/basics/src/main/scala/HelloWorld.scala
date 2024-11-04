val ages = Seq(42, 61, 29, 64)
def msg = "I was compiled by Scala 3. :)"

@main def try_hello: Unit =
  println("Hello world!")
  println(msg)
  println(ages.max)