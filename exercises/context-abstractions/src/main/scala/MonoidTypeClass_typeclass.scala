import typeclass.{Monoid, given}
object MonoidTypeClass_typeclass extends App{
  println("one" combine "two")
  println("one" <+> "two")
  println(1 combine 2)
  println(1 <+> 2)
  println(IntMonoid.unit <+> 1)
  println(2.5 combine 3.5)
  println(summon[Numeric[Double]].zero <+> 13.5)
  println(2.2 <+> (3.3 <+> 4.4))
  println("2" <+> ("3" <+> "4"))
}
