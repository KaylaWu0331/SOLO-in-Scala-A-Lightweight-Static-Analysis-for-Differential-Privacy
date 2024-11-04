object AssignmentsTuples extends App {
  def stats(seq: Seq[Double]): (Int, Double, Double, Double, Double) =
    assert(seq.size > 0)
    (seq.size, seq.sum, seq.sum/seq.size, seq.min, seq.max)

  val (count, sum, avg, min, max) = stats((0 to 100).map(_.toDouble))
  println((count, sum, avg, min, max))

}
