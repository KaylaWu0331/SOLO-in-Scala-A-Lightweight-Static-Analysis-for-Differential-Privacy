/* Pattern Matching & Given instances */
trait Witness
case object IntWitness extends Witness
case object StringWitness extends Witness

def useWitness(using Witness): String = summon[Witness].toString
// using Witness clause: requires a gicen Witness to be in scope when called
@main def TryUseWitness() =
  for given Witness <- Seq(IntWitness, StringWitness)
    // A loop over the objects, where the pattern given Witness types each object as a
    //given, but also scoped to the body of the for loop.
    do println(useWitness)

