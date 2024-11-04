/*
15. [Monads]:
    - ETW pattern: 
        E: extract, here get method
        T: transform, here transformer argument
        W: wrap, here flatwrap method
    - a type: here SafeValue  -> "constructor" in OOP, "pure" or "unit" in FP
    - two operations:
      to wrap a value into a value with additional features
      to transform a SafeValue into another SafeValue  -> bind or flatMap
*/
object Monads101 extends App:
  case class SafeValue[+T](private val internalValue: T):
    def get: T =synchronized{internalValue}
    def flatMap[S](transformer: T =>SafeValue[S]): SafeValue[S] = 
      synchronized{transformer(internalValue)}
  // assuming there is an "external" API called givemeSafeValue
  def givemeSafeValue[T](value: T): SafeValue[T] = SafeValue(value) 
  // Approach 1
  val safeStr: SafeValue[String] = givemeSafeValue("Hello Scala") // the original wrapper
  // extract string literal from safeStr using get method of type SafeValue
  val string = safeStr.get
  // transform
  val upperStr = string.toUpperCase()
  // wrap
  val upperSafeStr = SafeValue(upperStr)
  // Approach 2
  val upperSafeStr2 = safeStr.flatMap(s => SafeValue(s.toUpperCase()))
  println(safeStr)
  println(string)
  println(upperStr)
  println(upperSafeStr)
  println(upperSafeStr2)

  