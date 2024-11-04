package accounting
/* Implicit Conversions in scala 3: to convert between types for other reactions.
* */
/* [Description:]
- define types to represent Dollars, Percentages, and a person’s Salary,
- where the gross salary and the percentage to deduct for taxes are encapsulated.
- When constructing a Salary instance, we want to allow users to enter Doubles.
 */
case class Dollars(amount: Double):
  override def toString = f"$$$amount%.2f"
  def +(d:Dollars): Dollars = Dollars(amount + d.amount)
  def -(d:Dollars): Dollars = Dollars(amount - d.amount)
  def *(p:Percentage): Dollars = Dollars(amount * p.toMultiplier)
  def / (d:Dollars): Dollars = Dollars(amount / d.amount)
object Dollars:
    val zero = Dollars(0.0)
case class Percentage(amount: Double):
  override def toString: String = f"${amount}%.2f%%"
  def toMultiplier: Double = amount/100.0
  def +(p: Percentage): Percentage = Percentage(amount + p.amount)
  def -(p: Percentage): Percentage = Percentage(amount - p.amount)
  def *(p: Percentage): Percentage = Percentage(toMultiplier * p.toMultiplier)
  def *(d: Dollars): Dollars = d * this
object Percentage:
  val hundredPercent = Percentage(100.0)
  val zero = Percentage(0.0)
case class Salary(gross: Dollars, taxes: Percentage):
  def net: Dollars = gross * (Percentage.hundredPercent - taxes)

@main def TryImplicitConversions() = {
  import scala.language.implicitConversions

  given Conversion[Double, Dollars] = d => Dollars(d) // a given conversion from Double to Dollars
  //FIXME 12:23 2023/2/1: Conversion[-T,+U]?
  //FIXME 12:23 2023/2/1: d=> Dollars(d)?
  given Conversion[Double, Percentage] = d => Percentage(d)
  val salary = Salary(100_000.0, 20.0)
  println(s"salary: $salary, net pay: ${salary.net}")

  /* A longer form for defining a conversion as an alias given */
  given Conversion[Int, Dollars] with
    def apply(i: Int): Dollars = Dollars(i.toDouble)
  val dollars: Dollars = 1
  println(s"Dollars converted from an Int: $dollars")
}




