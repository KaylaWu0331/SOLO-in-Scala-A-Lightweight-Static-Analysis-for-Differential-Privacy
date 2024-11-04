object Assignments extends App{
  case class Address(street: String, city: String, country: String)
  case class Person(name: String, age: Int, address: Address)
  val addr = Address("1 Scala Way", "CA", "USA")
  val dean = Person("Dean",29,addr)
//  val Person(name, age, Address(_, state, )) = dean
  val people = (0 to 4).map {
    i => Person(s"Name$i",10+i, Address(s"$i Main Street", "CA", "USA"))
  }
  println(people)
  val nas = for
    Person(name, age,Address(_, state, _)) <- people
  yield (name, age, state)
  println(nas)
}
//Vector(Person(Name0,10,Address(0 Main Street,CA,USA)), Person(Name1,11,Address(1 Main Street,CA,USA)), Person(Name2,12,Address(2 Main Street,CA,USA)), Person(Name3,13,Address(3 Main Street,CA,USA)), Person(Name4,14,Address(4 Main Street,CA,USA)))
//Vector((Name0,10,CA), (Name1,11,CA), (Name2,12,CA), (Name3,13,CA), (Name4,14,CA))
