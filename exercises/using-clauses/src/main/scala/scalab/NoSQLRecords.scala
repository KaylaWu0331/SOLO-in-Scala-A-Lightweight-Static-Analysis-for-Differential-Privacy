/*Context bound as a WITNESS
* - we only care about context bound instead of the instance itself
* */
// Description:
// a sketch of an API for data records with ad hoc shemas, like in some NoSQL databases
// form: Map[String, Any], key: field names; column values: unconstrained, so Any
// functionality: add method for adding column values to a row; get method for retrieving data records

package scalab
import scala.language.implicitConversions // for given?
import scala.util.Try
case class InvalidFieldName(name: String) extends RuntimeException(s"Invalid field name $name")
object Record:
  def make: Record = new Record(Map.empty)
  type Conv[T] = Conversion[Any, T] //FIXME 10:18 2023/2/10:This alias is necessary when we define the given ab inside the method TryScalaDB.
case class Record private (contents: Map[String, Any]):
//case class Record (contents: Map[String, Any]):
  import Record.Conv //FIXME 10:20 2023/2/10:  try comment
  def add[T:Conv](nameValue: (String, T)): Record =//3
//  Equivalent: def add[T](nameValue: (String, T))(using Conv[T]): Record =
// [T:Conv] Conv here is a context bound, only as a witness to constrain the allowed values for T
    Record(contents + nameValue)
  def get[T: Conv](colName: String): Try[T] = // 4
    Try {
      val conv = summon[Conv[T]]
      conv(col(colName))
    }
  private def col(colName: String): Any =
    contents.getOrElse(colName, throw InvalidFieldName(colName))

@main def TryScalaDB =
  import Record.Conv //FIXME 10:28 2023/2/10: Try comment
  given Conv[Int] = _.asInstanceOf[Int] // 5
  given Conv[Double] = _.asInstanceOf[Double]
  given Conv[String] = _.asInstanceOf[String]
  given ab[A: Conv, B: Conv]: Conv[(A,B)] = _.asInstanceOf[(A,B)]
  val rec = Record.make.add("one" -> 1).add("two" -> 2.2).add("three" -> "THREE")
    .add("four" -> (4.4, "four")).add("five" -> (5,("five", 5.5)))

  val one = rec.get[Int]("one")
  val two = rec.get[Double]("two")
  val four = rec.get[(Double,String)]("four")
  val five = rec.get[(Int,(String,Double))]("five")

  val oneMissmatch = rec.get[Double]("one") // 6
  val oneError = rec.get[Int]("int")
//  val error = rec.get[Byte]("byte")
  println(s"one, two, four, five ->\n $one, $two, $four,\n $five")
//FIXME 11:22 2023/2/10: Common Error: println(one, two, four, five)
  println(s"wrong use: $oneMissmatch, \n $oneError")
// the get method:
// wrong combination of types in givens scope and elements: can get the whole record mapping
// types not in givens scope: an error during compilation




