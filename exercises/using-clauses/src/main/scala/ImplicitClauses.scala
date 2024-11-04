/* Context Bound by implicit parameters in Scala 2
* - implement a simple type that wraps sequences for convenient sorting in scala 2 */
case class SortableSeq1[A](seq: Seq[A]) {
  def sortBy1[B](transform: A => B)(implicit o: Ordering[B]): SortableSeq1[A] =
  // transform: to convert A to B
  // implicit o:Ordering[B] implicit param is ac context bound, which binds the output B
  // in other words, B is context bounded by Ordering[B]
    SortableSeq1(seq.sortBy(transform)(o))
  def sortBy2[B: Ordering](transform: A => B): SortableSeq1[A] =
    // [B: Ordering] context bound
    SortableSeq1(seq.sortBy(transform)(implicitly[Ordering[B]]))
    // while we define a name for the Ordering parameter, o,
    // the method sortBy needs a name for context bound Ordering[B]
    // hence, the method Predef.implicitly is called, to bind the implicit Ordering that is in scope,
    // so it can be passed as an argument
}
val seq1 = SortableSeq1(Seq(1,3,5,2,4))
@main def defaultOrdering1() = {
  println(seq1.sortBy1(i => i))
  println(seq1.sortBy2(i => -i))
}
@main def oddEvenOrdering1() = {
  implicit val oddEven: Ordering[Int] = new Ordering[Int]:
    def compare(m: Int, n: Int): Int = m % 2 compare n % 2 match
      case 0 => m compare n // same parity, compare m and n directly
      case other => other // different parity? then?//FIXME 10:37 2023/2/3: other equals to either -1, or 1

  println(seq1.sortBy1(i => i))
  println(seq1.sortBy2(i => -i))
}

/* Context Bound by using clauses in Scala 3 */
case class SortableSeq2[A](seq: Seq[A]):
  def sortBy3[B](transform: A => B)(using o: Ordering[B]): SortableSeq2[A] =
    SortableSeq2(seq.sortBy(transform)(o))
  def sortBy4[B](transform: A => B)(using Ordering[B]) : SortableSeq2[A] =
    SortableSeq2(seq.sortBy(transform)(summon[Ordering[B]]))
  def sortBy5[B: Ordering](transform: A => B): SortableSeq2[A] =
    SortableSeq2(seq.sortBy(transform)(summon[Ordering[B]]))

val seq2 = SortableSeq2(Seq(1,3,5,2,4))
@main def defaultOrdering2() =
  println(seq2.sortBy3(i => -i))
  println(seq2.sortBy4(i => -i))
  println(seq2.sortBy5(i => -i))
@main def oddEvenOrdering2() =
  implicit val oddEven: Ordering[Int] = new Ordering[Int]:
    def compare(m: Int, n: Int): Int = m % 2 compare n % 2 match
      case 0 => m compare n // same parity, compare m and n directly
      case other => other // different parity? then?//FIXME 10:37 2023/2/3: other equals to either -1, or 1
  println(seq2.sortBy3(i => -i))
  println(seq2.sortBy4(i => -i))
  println(seq2.sortBy5(i => -i))
@main def evenOddGivenOrdering() =
  // use a given instance instead of an implicit val in oddEvenOrdering
  given evenOdd: Ordering[Int] with
    def compare(m: Int, n: Int): Int = m%2 compare n%2 match
      case 0 => m compare n
      case other => -other //FIXME 10:58 2023/2/3: first even then odd
  println(seq2.sortBy3(i => -i)) // the given evenOdd is implicit
  println(seq2.sortBy4(i => -i))
  println(seq2.sortBy5(i => -i))

  println(seq2.sortBy3(i => -i)(using evenOdd)) // the given evenOdd is explicit in using clause
  println(seq2.sortBy4(i => -i)(using evenOdd))
  println(seq2.sortBy5(i => -i)(using evenOdd))



