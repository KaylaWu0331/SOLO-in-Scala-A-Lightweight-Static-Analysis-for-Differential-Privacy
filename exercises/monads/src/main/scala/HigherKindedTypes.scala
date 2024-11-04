/**
  *
  *  Categories(401) -> Higher-kinded Types(P388-391) -> Folding and Reducing (P210-212)
  * [Folding and Reducing]:
    - Both are operations for shrinking a collection down to a smaller collection or a single value, 
    so they are many-to-one operations.
    - reduceLeftOption method is a safer way than reduceLeft method, when dealing with an empty instance
    (avoid an exception)
    - Use cases of folding: implementation of mapping, flat mapping and filtering
  */
  object FoldLeft:
    // apply method for Seq collections:
    def apply[OUT, IN](seed: OUT)(seq: Seq[IN])(f: (OUT, IN) => OUT): OUT = 
        var acc = seed
        seq.foreach(ele => acc = f(acc, ele))
        acc
    // apply method for Option collections:
    def apply[OUT, IN](seed: OUT)(opt: Option[IN])(f: (OUT, IN) => OUT): OUT =
        opt match
            case Some(ele) => f(seed, ele)
            case None => seed

  def main(args: Array[String]): Unit = {
    val int1 = Seq(1,2,3,4,5,6).reduceLeft (_ + _)
    val int2 = Seq(1,2,3,4,5,6).foldLeft(15)(_ + _)
    val opt1 = Seq.empty[Int].reduceLeftOption(_ + _)
    val opt2 = Seq(1,2,3,4,5,6).reduceLeftOption(_ * _)
    println(int1)
    println(int2)
    println(opt1)
    println(opt2)
    // Use cases of folding:
    val vec = Vector(1, 2, 3, 4, 5, 6)
    // vec1: mapping
    val vec1 = vec.foldLeft(Vector.empty[String]){(vec, x) => vec :+ ("[" + x + "]")}
    println(vec1)
    val vec1a = vec.foldRight(Vector.empty[String]){(ele, seed) => ("[" + ele + "]") +: seed}
    println(vec1a)
    // vec2: to check op argument in foldLeft method
    val vec2 = vec.foldLeft(Vector("a")) {(seed, ele) => seed :+ ("[" + ele + "]")}
    println(vec2)
    // vec3: filtering
    val vec3 = vec.foldLeft(Vector.empty[Int]){(seed, ele) => if ele % 2 == 0 then seed else seed :+ ele}
    println(vec3)
    // vec4a: flatmappping
    val vec4a = vec.foldLeft(Vector.empty[Seq[Int]]){(seed, ele) => seed :+ (1 to ele)}
    println(vec4a)
    val vec4 = vec4a.flatten
    println(vec4)
    // test for FoldLeft object:
    println(FoldLeft("(0)")(List(1, 2, 3))((seed, ele) => s"($seed $ele)"))
    println(FoldLeft(0->"(0)")(Vector(1 -> "one", 2 -> "two", 3 -> "three")){case ((sx,sy),(ix,iy)) => (sx+ix, s"($sy $iy)")})
    println(FoldLeft(1.1)(Some(1.1))(_+_))

    object HKFoldLeft:
        // apply method: m: the instance of a higher-kinded type M[IN], f: performs the fold for each element
        def apply[OUT, IN, M[IN]](seed: OUT)(m: M[IN])(f: (OUT, IN) => OUT)(using Folder[M]): OUT = 
            summon[Folder[M]](seed, m, f)
        // trait Folder: abstracts over higher-kinded types with one parameter
        // its type parameter is contravariant, 
        trait Folder[-M[_]]:
            def apply[OUT, IN](seed: OUT, m: M[IN], f: (OUT, IN) => OUT): OUT

        given Folder[Iterable] with
            def apply[OUT, IN](seed: OUT, iter: Iterable[IN], f: (OUT, IN) => OUT): OUT =
                var acc =  seed
                iter.foreach(ele => acc = f(acc, ele))
                acc

        given Folder[Option] with
            def apply[OUT, IN](seed: OUT, opt: Option[IN], f: (OUT, IN) => OUT): OUT =
                opt match 
                    case Some(ele) => f(seed, ele)
                    case None => seed
    // test for HKFoldLeft object:
    import HKFoldLeft.{given, *}
    println(HKFoldLeft("(0)")(List(1, 2, 3))((seed, ele) => s"($seed $ele)"))
    println(HKFoldLeft(0->"(0)")(Vector(1 -> "one", 2 -> "two", 3 -> "three")){case ((sx,sy),(ix,iy)) => (sx+ix, s"($sy $iy)")})
    println(HKFoldLeft(1.1)(Some(1.1))(_+_))
    println(summon[Folder[Iterable]])
    println(summon[Folder[Option]])

  }
