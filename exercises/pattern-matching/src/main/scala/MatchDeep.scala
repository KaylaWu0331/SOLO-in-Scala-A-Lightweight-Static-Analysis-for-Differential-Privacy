object MatchDeep extends App{
  case class Address(street: String, city: String)
  case class Person(name: String, age: Int, address: Address)

  val alice = Person("Alice", 25, Address("1 Scala Lane", "Chicago"))
  val bob = Person("Bob", 29, Address("2 Java Ave.", "Miami"))
  val charlie = Person("Charlie", 32, Address("3 Python Ct.", "Boston"))
  val julia = Person("Julia", 30, Address("4 Solidity Street","New York"))

  val results = Seq(alice, bob, charlie,julia).map{
    case p @ Person("Alice", age, a @ Address(_, "Chicago")) =>
      s"Hi Alice! $p"
    case p @ Person("Bob", 29, a @ Address(str,city)) =>
      s"Hi ${p.name}! age ${p.age}, in ${a}"
    case p @ Person(name, 30, a @ Address(str, city)) =>
      s"Hi ${p.age}-year-old ${p.name}, you live in ${a.street}, ${a.city}"
    case p @ Person(name, age, Address(str, city)) =>
      s"Who are you, $name (age: $age, city = $city)?"
  }
  println(results.toString)
}
// List(
// Hi Alice! Person(Alice,25,Address(1 Scala Lane,Chicago)),
// Hi Bob! age 29, in Address(2 Java Ave.,Miami),
// Who are you, Charlie (age: 32, city = Boston)?)
