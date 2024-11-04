import scala.util.Random
// Source of the dataset:
// https://raw.githubusercontent.com/OpenMined/PyDP/dev/examples/Tutorial_4-Launch_demo/data/01.csv

case class Dataset(first_name: String, last_name: String, sales_amount: Double)

val originalDataset = List(
    Dataset("Osbourne","Gillions",31.94),
    Dataset("Glynn","Friett",12.46),
    Dataset("Jori","Blockley",191.14),
    Dataset("Garald","Dorian",126.58),
    Dataset("Mercy","Pilkington",68.32),
    Dataset("Elle","McConachie",76.91),
    Dataset("Ryun","Snadden",53.27),
    Dataset("Chelsea","Westrope",109.16),
    Dataset("Jack","Thexton",46.63),
    Dataset("Jude","Marczyk",17.46)
)
val modifiedDataset = List(
    Dataset("Glynn","Friett",12.46),
    Dataset("Jori","Blockley",191.14),
    Dataset("Garald","Dorian",126.58),
    Dataset("Mercy","Pilkington",68.32),
    Dataset("Elle","McConachie",76.91),
    Dataset("Ryun","Snadden",53.27),
    Dataset("Chelsea","Westrope",109.16),
    Dataset("Jack","Thexton",46.63),
    Dataset("Jude","Marczyk",17.46)
)
// =================================================================================================
// RAW: sum, count and average queries
// =================================================================================================
def rawQuerySum(data: List[Dataset], upperBound: Double): Double =
    val sum = data.filter(_.sales_amount < upperBound).map(_.sales_amount).sum
    sum

def rawQueryCount(data: List[Dataset], upperBound: Double): Int =
    data.count(_.sales_amount < upperBound)

def rawQueryAverage(data: List[Dataset], upperBound: Double): Double = 
    val sum = rawQuerySum(data, upperBound)
    val count = rawQueryCount(data, upperBound)
    val average = sum / count
    average
// =================================================================================================
// LAPLACIAN NOISE: sum, count and average queries
// =================================================================================================
def addLaplaceNoise(x: Double, sensitivity: Double, epsilon: Double): Double = 
    val b = sensitivity / epsilon
    val rand = new Random()
    val u = rand.nextDouble() - 0.5
    var xWithNoise = x + b * math.signum(u) * math.log(1.0 - 2.0 * math.abs(u))
    while (xWithNoise < 0 || xWithNoise > sensitivity) {
        xWithNoise = x + b * (rand.nextDouble() - 0.5) * math.log(1.0 - 2.0 * math.abs(rand.nextDouble() - 0.5))
    }
    xWithNoise

def laplaceNoiseQuerySum(data: List[Dataset], upperBound: Double): Double =
    val listWithNoise = data.map(dataset => addLaplaceNoise(dataset.sales_amount, sensitivity = upperBound, epsilon = 1))
    val sumWithNosie = listWithNoise.sum
    sumWithNosie

def laplaceNoiseQueryCount(data:List[Dataset], upperBound: Double):Int =
    val count = data.count(_.sales_amount < upperBound)
    count + addLaplaceNoise(count, sensitivity = 1, epsilon = 1).round.toInt

def laplaceNoiseQueryAverage(data: List[Dataset], upperBound: Double): Double = 
    val sum = laplaceNoiseQuerySum(data, upperBound)
    val count = laplaceNoiseQueryCount(data, upperBound)
    val average = sum / count
    average
// =================================================================================================
// GAUSSIAN NOISE: sum, count and average queries
// =================================================================================================
def addGaussNoise(x: Double, sensitivity: Double, epsilon: Double, delta: Double): Double = 
    val variance = math.sqrt(2 * sensitivity * sensitivity * math.log10(1.25 / delta) / (epsilon * epsilon))
    val rand = new Random()
    var xWithNoise = x + variance * rand.nextGaussian()
    while (xWithNoise < 0 || xWithNoise > sensitivity) {
        xWithNoise = x + variance * rand.nextGaussian()
    }
    xWithNoise
    
def gaussNoiseQuerySum(data: List[Dataset], upperBound: Double): Double =
    val listWithNoise = data.map(dataset => addGaussNoise(dataset.sales_amount, sensitivity = upperBound, epsilon = 1, delta = 0.01))
    val sumWithNosie = listWithNoise.sum
    sumWithNosie

def gaussNoiseQueryCount(data: List[Dataset], upperBound: Double):Int =
    val count = data.count(_.sales_amount < upperBound)
    count + addGaussNoise(count, sensitivity = 1, epsilon = 1, delta = 0.01).round.toInt

def gaussNoiseQueryAverage(data: List[Dataset], upperBound: Double): Double = 
    val sum = gaussNoiseQuerySum(data, upperBound)
    val count = gaussNoiseQueryCount(data, upperBound)
    val average = sum / count
    average
// =================================================================================================
// SOLO
// =================================================================================================
// Primitives:
// ------------------------------------------------------------------------------------
enum Nat:
    case Zero
    case Succ(n: Nat)
    case Infinity
    def toInt: Int = this match 
        case Zero => 0
        case Succ(n) => n.toInt + 1
        case Infinity => throw new IllegalArgumentException("Cannot convert infinity to Int")
    def + (n2: Nat): Nat = (this, n2) match
        case (Zero, _) => n2
        case (_, Zero) => this
        case (Succ(n), _) => Succ(n + n2)
        case (_, Succ(n)) => Succ(this + n)
        case _ => Infinity
    def >=(n2: Nat): Boolean = (this, n2) match
        case (Infinity, _) => true
        case (_, Infinity) => false
        case (Zero, Zero) => true
        case (Zero, _) => false
        case (_, Zero) => true
        case (Succ(m1), Succ(m2)) => m1 >= m2
    def *(n2: Nat): Nat = (this, n2) match
      case (Zero, _) | (_, Zero) => Zero
      case (Succ(Zero), n2) => n2
      case (n1, Succ(Zero)) => n1
      case (Succ(n1), n2) => n2 + (n1 * n2)
      case (Infinity, n2) if n2 == Infinity => Infinity
      case (Infinity, _) | (_, Infinity) => Infinity

extension (n: Int)
    def toNat: Nat =
        if n == 0 then Nat.Zero
        else if n > 0 then Nat.Succ((n - 1).toNat)
        else Nat.Infinity

type Sensitivity = Nat

type Source = String

case class SEnv(seq: Seq[(Source,Sensitivity)])

enum NMetric: // distance metrics for numeric types
    case Diff
    case Disc

    def absolDis[T: Numeric](x: T, y: T): T = 
        val numeric = summon[Numeric[T]]
        val distance = numeric.minus(x, y)
        numeric.abs(distance)

    def discDis[T: Numeric](x: T, y: T): T = 
        // if (x == y) 0 else 1
        val numeric = summon[Numeric[T]]
        (x,y) match 
            case (a,b) if (a == b)  => numeric.zero
            case _  => numeric.one

    def dis[T: Numeric](x: T, y: T): T =
        this match
            case Diff => absolDis(x,y)
            case Disc => discDis(x,y)

case class SDouble (m: NMetric, s: SEnv, unSDouble: Double) 

enum CMetric: // distance metrics for compound types
    case L1
    case L2
    def distance(x: Double, y: Double): Double =
        this match
            case L1 => math.abs(x - y)
            case L2 => math.sqrt((x - y) * (x - y))

case class SList[X](cmetric: CMetric,nmetric: NMetric, list: List[X], senv: SEnv)
// ------------------------------------------------------------------------------------------------
// Sensitivity analysis in sum queries
// ------------------------------------------------------------------------------------------------
def MaxNat(n1: Nat, n2: Nat): Nat =  // works for MaxSens, finds the largest sensitivity, which has Nat type
  if n1 >= n2 then n1
  else n2

def MaxSens(s: SEnv): Nat = s.seq match // finds the largest sensitivity as the upper bound for clip function
  case Nil => Nat.Zero
  case (_,n) +: r => MaxNat(n,MaxSens(SEnv(r)))

def clip(slist: SList[SDouble], upperbound: Double): SList[SDouble] = // transforms Disc metric in SDouble to Diff metric to make sensitivity bounded, the sensitivity would be clipped to the upper bound
    require(slist.cmetric == CMetric.L1 && slist.nmetric == NMetric.Disc, "Invalid input" ) // constrain the type of arguments to be L1List(SDboule Dsic) senv
    val slistWithDiff = SList(cmetric = CMetric.L1, // uses Diff metric
        nmetric = NMetric.Diff, 
        list = slist.list.map(sd => SDouble(m = NMetric.Diff, s = SEnv(Seq((sd.s.seq(0)._1, sd.unSDouble.round.toInt.toNat))),unSDouble = sd.unSDouble)),
        senv = slist.senv
        )
    // var allSenvOfSDouble = Seq.empty[(Source, Sensitivity)]
    // for (sd <- slistWithDiff.list) (allSenvOfSDouble = allSenvOfSDouble :+ sd.s.seq(0)) 
    // val maxSens = MaxSens(SEnv(allSenvOfSDouble)) // get the upper bound
    val clippedList = slist.list.map( ele => // cut the unbounded sensitivity with upperbound
            if (ele.unSDouble > upperbound) upperbound
            else if (ele.unSDouble < 0) 0
            else ele.unSDouble    
    )
    val clippedSList = SList(cmetric = CMetric.L1, nmetric = NMetric.Diff, 
    list = clippedList.map( value => SDouble(m = NMetric.Diff, s = SEnv(Seq(("source", value.toInt.toNat))), unSDouble = value)),
    senv = SEnv(Seq((slist.senv.seq(0)._1, upperbound.toInt.toNat)))
    )
    clippedSList

def sum(clippedSList: SList[SDouble]): SDouble =
    require(clippedSList.cmetric == CMetric.L1 && clippedSList.nmetric == NMetric.Diff, "Invalid input")
    var sum: Double = 0.0
    for (sd <- clippedSList.list) {
        var sens= sd.s.seq(0)._2
        sum += sd.unSDouble + addLaplaceNoise(sd.unSDouble, sensitivity = sens.toInt.toDouble, epsilon = 3)
    } 
    val sumInSDouble = SDouble(m = clippedSList.nmetric, s= clippedSList.senv, unSDouble = sum)
    sumInSDouble

def SOLOSumQuery(data: List[Dataset], source: String, upperbound: Double):SDouble = 
    val db: SList[SDouble] = SList(cmetric = CMetric.L1, nmetric = NMetric.Disc, 
    list = data.map(ele => SDouble(m = NMetric.Disc, s = SEnv(Seq((source, 1.toNat))), unSDouble = ele.sales_amount)), 
    senv = SEnv(Seq(( source, 1.toNat))))
    val clippedSList = clip(db, upperbound)
    val sumInSDouble = sum(clippedSList)
    sumInSDouble

def count(slist:SList[SDouble], upperbound: Double): SDouble =
    require(slist.cmetric == CMetric.L1 && slist.nmetric == NMetric.Disc, "Invalid input")
    var count = 0
    for (sd <- slist.list) if (sd.unSDouble < upperbound) count += 1
    val countWithNoise = count + addLaplaceNoise(count, sensitivity = 1, epsilon = 1).round.toInt
    SDouble(m = NMetric.Disc, s = SEnv(Seq((slist.senv.seq(0)._1, 1.toNat))), unSDouble = countWithNoise.toDouble)

def SOLOCountQuery(data:List[Dataset], source: String, upperbound: Double): SDouble =
    val db: SList[SDouble] = SList(cmetric = CMetric.L1, nmetric = NMetric.Disc, 
    list = data.map(ele => SDouble(m = NMetric.Disc, s = SEnv(Seq((source, 1.toNat))), unSDouble = ele.sales_amount)), 
    senv = SEnv(Seq(( source, 1.toNat))))
    val countVal = count(db, upperbound)
    countVal

def SOLOAverageQuery(data:List[Dataset], source: String, upperbound: Double): SDouble = 
    val sum = SOLOSumQuery(data, source, upperbound)
    val count = SOLOCountQuery(data, source, upperbound)
    SDouble(m= NMetric.Diff, s= SEnv(Seq((source, sum.s.seq(0)._2 * count.s.seq(0)._2))), unSDouble = sum.unSDouble / count.unSDouble)
// ------------------------------------------------------------------------------------------------
// Laplace mechanism with ε-differential privacy using privacy monad
// ------------------------------------------------------------------------------------------------
// Primitives:
// ------------------------------------------------------------------------------------------------
case class TLRat(numerator: BigInt, denominator: BigInt):
    require(denominator != 0, "Denominator cannot be zero.")
    require(denominator > 0, "Denominator must be positive.")
    private val gcdValue = (numerator.abs).gcd(denominator.abs)
    val num: BigInt = numerator / gcdValue
    val den: BigInt = denominator / gcdValue

    def toDouble: Double = num.toDouble/den.toDouble

    def +(that: TLRat): TLRat = 
        val newNum = num * that.den + that.num * den
        val newDen = den * that.den
        TLRat(newNum, newDen)

enum EpsPrivacyCost:
    case InfEps
    case EpsCost(numerator: BigInt, denominator: BigInt)

    def toTLRat: TLRat = this match
        case InfEps => TLRat(0, 1)
        case EpsCost(n, d) => TLRat(n, d)

case class EpsPrivEnv(seq:Seq[(Source,EpsPrivacyCost)])

case class EpsSeqComp(epe1:EpsPrivEnv, epe2:EpsPrivEnv): // implements the sequential composition theorem in section 2
    def seqComp: EpsPrivEnv = 
        val sumTLRat = epe1.seq(0)._2.toTLRat + epe2.seq(0)._2.toTLRat
        sumTLRat match
            case TLRat(0,1) => EpsPrivEnv(Seq((epe1.seq(0)._1 + epe2.seq(0)._1 ,EpsPrivacyCost.InfEps)))
            case TLRat(n,d) => EpsPrivEnv(Seq((epe1.seq(0)._1 + epe2.seq(0)._1 ,EpsPrivacyCost.EpsCost(n,d))))

trait Monad[M[_]]:
    def pure[A](value: A): M[A] // like return operation, generates identity in the monoidal structure
    def bind[A, B](monad: M[A])(f: A => M[B]): M[B] 
    // computes in sequence way: takes a monad instance with type of M[A], and function f,which transform A to M[B]
    // returns a monad instance with type of M[B]
// ------------------------------------------------------------------------------------------------
// Laplace mechanism with ε-differential privacy using privacy monad
// ------------------------------------------------------------------------------------------------
case class EpsPrivacyMonad[A](p: EpsPrivEnv, value: A)

given Monad[EpsPrivacyMonad] with // a monad for EpsPrivacyMonad
    def pure[A](value: A): EpsPrivacyMonad[A] = EpsPrivacyMonad(EpsPrivEnv(Seq.empty), value)
    def bind[A, B](monad: EpsPrivacyMonad[A])(f: A => EpsPrivacyMonad[B]): EpsPrivacyMonad[B] =
        val a = monad.value
        val env1 = monad.p
        val b = f(a).value
        val env2 = f(a).p
        EpsPrivacyMonad(EpsSeqComp(env1, env2).seqComp, b)

def TruncateNat(n1: EpsPrivacyCost)(n2: Nat): EpsPrivacyCost = n2 match // works for TruncateSens, to transform Nat number to rational number
    case Nat.Zero => EpsPrivacyCost.InfEps
    case _ => n1

def TruncateSens(n: EpsPrivacyCost)(s: SEnv): EpsPrivEnv = s.seq match // transform sensitive environment to private environment
    case Nil => EpsPrivEnv(Nil)
    case (o,n2) +: r => EpsPrivEnv((o, TruncateNat(n)(n2)) +: TruncateSens(n)(SEnv(r)).seq)

def listLaplace(epsilon: EpsPrivacyCost, slist: SList[SDouble]): EpsPrivacyMonad[List[Double]] =
    require(slist.cmetric == CMetric.L1 && slist.nmetric == NMetric.Diff, "Invalid Input") // constrains the type of the argument: L1List (SDouble Diff) senv
    val s = slist.senv
    var allSenvOfSDouble = Seq.empty[(Source, Sensitivity)]
    for (sd <- slist.list) (allSenvOfSDouble = allSenvOfSDouble :+ sd.s.seq(0)) 
    val maxSens = MaxSens(SEnv(allSenvOfSDouble)) //according to the type of slist(Diff metric), needs to compute upperbound
    val valueWithNoise = slist.list.map(sd => 
    addLaplaceNoise(x= sd.unSDouble, sensitivity = maxSens.toInt.toDouble, epsilon= epsilon.toTLRat.toDouble))
    EpsPrivacyMonad(TruncateSens(epsilon)(s), valueWithNoise) // sequential computation: first truncate sensitive environment to private environment, then add Laplacian noise to List[Double]

def SOLOMonadQuery(epsilon: EpsPrivacyCost, source: String, data: List[Dataset]): EpsPrivacyMonad[List[Double]] =
    val db: SList[SDouble] = SList(cmetric = CMetric.L1, nmetric = NMetric.Diff, 
    list = data.map(ele => SDouble(m = NMetric.Diff, s = SEnv(Seq((source, ele.sales_amount.round.toInt.toNat))), unSDouble = ele.sales_amount)), 
    senv = SEnv(Seq((source, 1.toNat))))
    val resultMonad = listLaplace(epsilon, db)
    resultMonad
// ------------------------------------------------------------------------------------------------
// Laplace mechanism with ε-differential privacy using privacy monad
// ------------------------------------------------------------------------------------------------
// Primitives:
// ------------------------------------------------------------------------------------------------
enum TLReal:
    case PositiveInfinity 
    case NegativeInfinity
    case Finite(value: Double)

    def toDouble: Double = this match 
        case PositiveInfinity => Double.PositiveInfinity
        case NegativeInfinity => Double.NegativeInfinity
        case Finite(value) => value

    def sqrt: TLReal = this match
        case PositiveInfinity => PositiveInfinity
        case NegativeInfinity => throw new ArithmeticException("Square root of negative infinity is undefined")
        case Finite(x) if x < 0 => throw new ArithmeticException("Square root of negative number is undefined")
        case Finite(x) => Finite(math.sqrt(x))

    def +(other: TLReal): TLReal = (this, other) match 
        case (Finite(x), Finite(y)) => Finite(x + y)
        case (PositiveInfinity, NegativeInfinity) => throw new IllegalArgumentException("Cannot add infinity to negative infinity")
        case (NegativeInfinity, PositiveInfinity) => throw new IllegalArgumentException("Cannot add negative infinity to infinity")
        case (PositiveInfinity, _) => PositiveInfinity
        case (_, PositiveInfinity) => PositiveInfinity
        case (NegativeInfinity, _) => NegativeInfinity
        case (_, NegativeInfinity) => NegativeInfinity

    def *(other: TLReal): TLReal = (this, other) match
        case (Finite(x), Finite(y)) => Finite(x * y)
        case (PositiveInfinity, Finite(x)) if x > 0 => PositiveInfinity
        case (Finite(x), PositiveInfinity) if x > 0 => PositiveInfinity
        case (NegativeInfinity, Finite(x)) if x < 0 => PositiveInfinity
        case (Finite(x), NegativeInfinity) if x < 0 => PositiveInfinity
        case (PositiveInfinity, PositiveInfinity) => PositiveInfinity
        case (NegativeInfinity, NegativeInfinity) => PositiveInfinity
        case (PositiveInfinity, NegativeInfinity) => NegativeInfinity
        case (NegativeInfinity, PositiveInfinity) => NegativeInfinity
        case _ => throw new ArithmeticException("Multiplication is undefined for infinity times zero or NaN")

    def /(other: TLReal): TLReal = (this, other) match
        case (Finite(x), Finite(y)) if y != 0 => Finite(x / y)
        case (PositiveInfinity, PositiveInfinity) => throw new ArithmeticException("Division of infinity by infinity is undefined")
        case (NegativeInfinity, NegativeInfinity) => throw new ArithmeticException("Division of negative infinity by negative infinity is undefined")
        case (PositiveInfinity, Finite(x)) if x != 0 => PositiveInfinity
        case (NegativeInfinity, Finite(x)) if x != 0 => NegativeInfinity
        case (Finite(x), PositiveInfinity) => Finite(0)
        case (Finite(x), NegativeInfinity) => Finite(0)
        case (PositiveInfinity, NegativeInfinity) => throw new ArithmeticException("Division of infinity by negative infinity is undefined")
        case (NegativeInfinity, PositiveInfinity) => throw new ArithmeticException("Division of negative infinity by infinity is undefined")
        case (_, Finite(0)) => throw new ArithmeticException("Division by zero is undefined")
        case _ => throw new ArithmeticException("Division is undefined for infinity divided by infinity or NaN")

    def ln: TLReal = this match
        case PositiveInfinity => PositiveInfinity
        case NegativeInfinity => throw new ArithmeticException("Logarithm of negative infinity is undefined")
        case Finite(x) if x <= 0 => throw new ArithmeticException("Logarithm of non-positive number is undefined")
        case Finite(x) => Finite(math.log(x))

enum EDPrivacyCost:
    case InfED
    case EDCost(value: TLReal)

case class EDEnv(seq: Seq[(Source, TLReal, TLReal)])

case class EDSeqComp(ed1:EDEnv, ed2:EDEnv): // implements advanced composition in section 6
    def seqComp: EDEnv = 
        val epsilon1 = ed1.seq(0)._2
        val delta1 = ed1.seq(0)._3
        val epsilon2 = ed2.seq(0)._2
        val delta2 = ed2.seq(0)._3    
        val kInTLReal = TLReal.Finite(1) 
        val newEpsilon = (TLReal.Finite(2).`*`(epsilon1)).`*`((TLReal.Finite(2).`*`(kInTLReal).`*`((TLReal.Finite(1)./(delta2)).ln)).sqrt)
        val newDelta = delta2.`+`(kInTLReal.`*`(delta1))
        EDEnv(Seq((ed1.seq(0)._1 + ed2.seq(0)._1, newEpsilon, newDelta)))

def TruncateTLReal(n1: TLReal)(n2: Nat): TLReal = n2 match // works for TruncatePriv and transform Nat to real number
    case Nat.Zero => TLReal.Finite(0)
    case _ => n1

def TruncatePriv(epsilon:TLReal)(delta: TLReal)(s: SEnv): EDEnv = s.seq match // truncates sensitive environment to private environment including epsilon and delta
    case Nil => EDEnv(Nil)
    case (o,n2) +: r => EDEnv((o, TruncateTLReal(epsilon)(n2), TruncateTLReal(delta)(n2)) +: TruncatePriv(epsilon)(delta)(SEnv(r)).seq)

case class EDPrivacyMonad[A](p: EDEnv, value: A)

given Monad[EDPrivacyMonad] with
    def pure[A](value: A): EDPrivacyMonad[A] = EDPrivacyMonad(EDEnv(Seq.empty), value)
    def bind[A, B](monad: EDPrivacyMonad[A])(f: A => EDPrivacyMonad[B]): EDPrivacyMonad[B] =
        val a = monad.value
        val env1 = monad.p
        val b = f(a).value
        val env2 = f(a).p
        EDPrivacyMonad(EDSeqComp(env1, env2).seqComp, b)

def listGauss(epsilon: TLReal, delta: TLReal,  slist: SList[SDouble]): EDPrivacyMonad[List[Double]] = 
    // takes epsilon, delta and slist as arguments, return a EDPrivacyMonad, which truncates SEnv to EDEnv and adds Gaussian noise to list of Double 
    require(slist.cmetric == CMetric.L2 && slist.nmetric == NMetric.Diff, "Invalid Input")
    val s = slist.senv
    var allSenvOfSDouble = Seq.empty[(Source, Sensitivity)]
    for (sd <- slist.list) (allSenvOfSDouble = allSenvOfSDouble :+ sd.s.seq(0)) 
    val maxSens = MaxSens(SEnv(allSenvOfSDouble))
    val valueWithNoise = slist.list.map(sd => 
    addGaussNoise(x= sd.unSDouble, sensitivity = maxSens.toInt.toDouble, epsilon= epsilon.toDouble, delta = delta.toDouble))
    EDPrivacyMonad(TruncatePriv(epsilon)(delta)(s), valueWithNoise)

def SOLOMonadQuery2(epsilon: TLReal, delta:TLReal, source: String, data: List[Dataset]): EDPrivacyMonad[List[Double]] =
    val db: SList[SDouble] = SList(cmetric = CMetric.L2, nmetric = NMetric.Diff, 
    list = data.map(ele => SDouble(m = NMetric.Diff, s = SEnv(Seq((source, ele.sales_amount.round.toInt.toNat))), unSDouble = ele.sales_amount)), 
    senv = SEnv(Seq((source, 1.toNat))))
    val resultMonad = listGauss(epsilon, delta, db)
    resultMonad
    
def main(args: Array[String]): Unit = {
    // =================================================================================================
    // Test for 3 kinds of sum queries:
    // ================================================================================================
    val originalSum = "%.2f".format(rawQuerySum(originalDataset, 200)).toDouble
    val modifiedSum = "%.2f".format(rawQuerySum(modifiedDataset, 200)).toDouble
    val actualDifference ="%.2f".format(originalSum - modifiedSum).toDouble
    val expectedDifference = originalDataset(0).sales_amount
    println("Test for 3 kinds of sum queries:")
    println("Raw sum query on the original dataset is " + originalSum)
    println("Raw sum query on the modified dataset is " + modifiedSum)
    println("The expected difference between 2 queries is " + expectedDifference)
    println("The actual difference between 2 queries is " + actualDifference)
    println()
    val originalSumWithLaplaceNoise = "%.2f".format(laplaceNoiseQuerySum(originalDataset, 200)).toDouble
    val modifiedSumWithLaplaceNoise = "%.2f".format(laplaceNoiseQuerySum(modifiedDataset, 200)).toDouble
    val actualDifferenceWithLaplaceNoise = "%.2f".format(originalSumWithLaplaceNoise - modifiedSumWithLaplaceNoise).toDouble
    println("Sum query with Laplacian noise on the original dataset is " + originalSumWithLaplaceNoise)
    println("Sum query with Laplacian noise on the modified dataset is " + modifiedSumWithLaplaceNoise)
    println("The expected difference between 2 queries is " + expectedDifference)
    println("The ectual difference between 2 queries is " + actualDifferenceWithLaplaceNoise)
    println()
    val originalSumWithGaussNoise = "%.2f".format(gaussNoiseQuerySum(originalDataset, 200)).toDouble
    val modifiedSumWithGaussNoise = "%.2f".format(gaussNoiseQuerySum(modifiedDataset, 200)).toDouble
    val actualDifferenceWithGaussNoise = "%.2f".format(originalSumWithGaussNoise - modifiedSumWithGaussNoise).toDouble
    println("Sum query with Gaussian noise on the original dataset is " + originalSumWithGaussNoise)
    println("Sum query with Gaussian noise on the modified dataset is " + modifiedSumWithGaussNoise)
    println("The expected difference between 2 queries is " + expectedDifference)
    println("The ectual difference between 2 queries is " + actualDifferenceWithGaussNoise)
    println()
    // =============================================================================================================================
    // Test for 3 kinds of count queries:
    // =============================================================================================================================
    val upperbound1 = 100
    val originalCount = rawQueryCount(originalDataset, upperbound1)
    val modifiedCount = rawQueryCount(modifiedDataset, upperbound1)
    val expectedOriginalCount = originalDataset.count(_.sales_amount < upperbound1)
    val exceptedModifiedCount = modifiedDataset.count(_.sales_amount < upperbound1)
    println("Test for 3 kinds of count queries:")
    println(s"Raw count query on original dataset: the number of people whose sales amount is smaller than $upperbound1 is $originalCount")
    println(s"Raw count query on modified dataset: the number of people whose sales amount is smaller than $upperbound1 is $modifiedCount")
    println(s"the expected number in original dataset is $expectedOriginalCount")
    println(s"the expected number in modified dataset is $exceptedModifiedCount")
    println()
    val originalCountWithLaplaceNoise = laplaceNoiseQueryCount(originalDataset,upperbound1)
    val modifiedCountWithLaplaceNoise = laplaceNoiseQueryCount(modifiedDataset,upperbound1)
    println(s"Count query with Laplacian noise on original dataset: the number of people whose sales amount is smaller than $upperbound1 is $originalCountWithLaplaceNoise")
    println(s"Count query with Laplacian noise on modified dataset: the number of people whose sales amount is smaller than $upperbound1 is $modifiedCountWithLaplaceNoise")
    println()
    val originalCountWithGaussNoise = gaussNoiseQueryCount(originalDataset,upperbound1)
    val modifiedCountWithGaussNoise = gaussNoiseQueryCount(modifiedDataset,upperbound1)
    println(s"Count query with Gaussian noise on original dataset: the number of people whose sales amount is smaller than $upperbound1 is $originalCountWithGaussNoise")
    println(s"Count query with Gaussian noise on modified dataset: the number of people whose sales amount is smaller than $upperbound1 is $modifiedCountWithGaussNoise")
    println()
    // =========================================================================================================================
    // Test for 3 kinds of average queries:
    // =============================================================================================================================
    val upperbound2 = 150
    val originalAverage = "%.2f".format(rawQueryAverage(originalDataset, upperbound2)).toDouble
    val modifiedAverage = "%.2f".format(rawQueryAverage(modifiedDataset, upperbound2)).toDouble
    val expectedOriginalAverage = originalDataset.filter(_.sales_amount < upperbound2).map(_.sales_amount).reduce(_+_) / originalDataset.count(_.sales_amount < upperbound2)
    val roundedExpectedOriginalAverage = "%.2f".format(expectedOriginalAverage).toDouble
    val expectedModifiedAverage = modifiedDataset.filter(_.sales_amount < upperbound2).map(_.sales_amount).reduce(_+_)  / modifiedDataset.count(_.sales_amount < upperbound2)
    val roundedExpectedModifiedAverage = "%.2f".format(expectedModifiedAverage).toDouble
    println("Test for 3 kinds of average queries:")
    println(s"Raw average query on original dataset: the average sales amount of people whose sales amount is smaller than $upperbound2 is $originalAverage")
    println(s"Raw average query on modified dataset: the average sales amount of people whose sales amount is smaller than $upperbound2 is $modifiedAverage")
    println(s"the expected average in original dataset is $roundedExpectedOriginalAverage")
    println(s"the expected average in modified dataset is $roundedExpectedModifiedAverage")
    println()
    val originalAverageWithLaplaceNoise = "%.2f".format(laplaceNoiseQueryAverage(originalDataset,upperbound2)).toDouble
    val modifiedAverageWithLaplaceNoise = "%.2f".format(laplaceNoiseQueryAverage(modifiedDataset,upperbound2)).toDouble
    println(s"Average query with Laplacian noise on original dataset: the average sales amount of people whose sales amount is smaller than $upperbound2 is $originalAverageWithLaplaceNoise")
    println(s"Average query with Laplacian noise on modified dataset: the average sales amount of people whose sales amount is smaller than $upperbound2 is $modifiedAverageWithLaplaceNoise")
    println()
    val originalAverageWithGaussNoise = "%.2f".format(gaussNoiseQueryAverage(originalDataset,upperbound2)).toDouble
    val modifiedAverageWithGaussNoise = "%.2f".format(gaussNoiseQueryAverage(modifiedDataset,upperbound2)).toDouble
    println(s"Average query with Gaussian noise on original dataset: the average sales amount of people whose sales amount is smaller than $upperbound2 is $originalAverageWithGaussNoise")
    println(s"Average query with Gaussian noise on modified dataset: the average sales amount of people whose sales amount is smaller than $upperbound2 is $modifiedAverageWithGaussNoise")
    println()
   // =============================================================================================================================
   // Test for SOLO sum queries:
   // =============================================================================================================================
    val SOLOSumOriginal = SOLOSumQuery(originalDataset, "originalDataset",200)
    val SOLOSumModified = SOLOSumQuery(modifiedDataset, "modifiedDataset", 200)
    val SOLOoriginalSumWithNoise = "%.2f".format(SOLOSumOriginal.unSDouble).toDouble
    val SOLOmodifiedSumWithNoise = "%.2f".format(SOLOSumModified.unSDouble).toDouble
    val SOLOactual1WithNoise = "%.2f".format(SOLOoriginalSumWithNoise - SOLOmodifiedSumWithNoise).toDouble
    println("SOLO sum query on the original dataset is " + SOLOoriginalSumWithNoise)
    println("SOLO sum query on the modified dataset is " + SOLOmodifiedSumWithNoise)
    println("The expected difference between 2 queries is " + expectedDifference)
    println("The actual difference between 2 queries is " + SOLOactual1WithNoise)
    println(s"The sensitivity of SOLO sum query on original dataset is: (${SOLOSumOriginal.s.seq(0)._1},${SOLOSumOriginal.s.seq(0)._2.toInt})")
    println(s"The sensitivity of SOLO sum query on modified dataset is: (${SOLOSumModified.s.seq(0)._1},${SOLOSumModified.s.seq(0)._2.toInt})")
    println()
    // =============================================================================================================================
    // Test for SOLO count queries:
    // =============================================================================================================================    
    val SOLOCountOriginal = SOLOCountQuery(originalDataset, "originalDataset", 100)
    val SOLOCountModified = SOLOCountQuery(modifiedDataset, "modifiedDataset", 100)
    println("Test for SOLO count queries:")
    println(s"SOLO count query on the original dataset is " +SOLOCountOriginal)
    println(s"SOLO count query on the modified dataset is " +SOLOCountModified)
    println()
    // =============================================================================================================================
    // Test for SOLO average queries:
    // ============================================================================================================================= 
    val SOLOAverageOriginal = SOLOAverageQuery(originalDataset, "originalDataset", 150)
    val SOLOAverageModified = SOLOAverageQuery(modifiedDataset, "modifiedDataset", 150)
    println("Test for SOLO average queries:")
    println("SOLO average query on the original dataset is " +SOLOAverageOriginal.unSDouble) 
    println("SOLO average query on the modified dataset is " +SOLOAverageModified.unSDouble)
    println(s"The sensitivity of SOLO average query on original dataset is: (${SOLOAverageOriginal.s.seq(0)._1},${SOLOAverageOriginal.s.seq(0)._2.toInt})")
    println(s"The sensitivity of SOLO average query on modified dataset is: (${SOLOAverageModified.s.seq(0)._1},${SOLOAverageModified.s.seq(0)._2.toInt})")
    println()
    // =============================================================================================================================
    // Test for SOLO EpsPrivacyMonad:
    // =============================================================================================================================
    val resultMonad1 = SOLOMonadQuery(epsilon = EpsPrivacyCost.EpsCost(numerator = 1, denominator =1),source = "originalDataset", data = originalDataset)
    val sumOriginal1 = resultMonad1.value.sum
    val resultMonad2 = SOLOMonadQuery(epsilon = EpsPrivacyCost.EpsCost(numerator = 1, denominator =1),source = "modifiedDataset", data = modifiedDataset)
    val sumModified1 = resultMonad2.value.sum
    println("Test for SOLO EpsPrivacyMonad:")
    println(s"The sum of embedding privacy environment and adding Laplacian Noise to the original dataset with epsilon-differential privacy using EpsPrivacyMonad is ${sumOriginal1} with ${resultMonad1.p}")
    println(s"The sum of embedding privacy environment and adding Laplacian Noise to the original dataset with epsilon-differential privacy using EpsPrivacyMonad is ${sumModified1} with ${resultMonad2.p}")
    println()
    // =============================================================================================================================
    // Test for SOLO EDPrivacyMonad:
    // =============================================================================================================================
    val resultMonad3 = SOLOMonadQuery2(epsilon = TLReal.Finite(1.0), delta = TLReal.Finite(0.01), source = "originalDataset", data = originalDataset)
    val sumOriginal2 = resultMonad3.value.sum
    val resultMonad4 = SOLOMonadQuery2(epsilon = TLReal.Finite(1.0), delta = TLReal.Finite(0.01), source = "modifiedDataset", data = modifiedDataset)
    val sumModified2 = resultMonad4.value.sum
    println("Test for SOLO EDPrivacyMonad:")
    println(s"The sum of embedding privacy environment and adding Gaussian Noise to the original dataset with (epsilon,delta)-differential privacy using EDPrivacyMonad is ${sumOriginal2} with ${resultMonad3.p}")
    println(s"The sum of embedding privacy environment and adding Gaussian Noise to the modified dataset with (epsilon,delta)-differential privacy using EDPrivacyMonad is ${sumModified2} with ${resultMonad4.p}")
}