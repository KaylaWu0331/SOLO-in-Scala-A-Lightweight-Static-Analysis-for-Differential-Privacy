/* 
to implement a pair construction idiom:
    1. implicit conversion in scala 2
        = from String to ArrowAssoc[A], the latter is implicit
        - a method with only one argument can be used as an operator
    2. extension method in scala 3
*/
implicit final class ArrowAssoc[A](private val self: A) {
    def ~> [B](y: B): (A, B) = (self, y) 
}

extension [A](a: A) def ~~> [B](b: B): (A, B) = (a,b)
extension [A,B](a:A) def ~~~> (b: B): (A,B) = (a,b)
def main(args: Array[String]): Unit = {
    println("one" ~> 1)
    println("one" ~~> 1)
    println("one" ~~~> 1)
}
