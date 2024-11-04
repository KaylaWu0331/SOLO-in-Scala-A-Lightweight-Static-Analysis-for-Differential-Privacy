val f1: Int => Seq[Int] = i => 0 until 10 by ((math.abs(i) % 10) + 1)
def func(i:Int): Seq[Int] =
    val lst = 0 until 10 by ((math.abs(i) % 10) + 1)
    lst
func(3)

