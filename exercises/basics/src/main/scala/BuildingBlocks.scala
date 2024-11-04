/* 
1. [type params in  abstract classes]:
    - means this abstract class is applicable for which type parameters

16. [convariant type parameters]:
    - For example, consider a class Container[+T] that holds an instance of T. 
    If Foo is a subtype of Bar, then Container[Foo] is considered a subtype of Container[Bar]. 
    This means that, for instance, you can assign a Container[Foo] to a variable of type Container[Bar].
    - In general, covariant type parameters can be used when the type parameter is only used in output positions 
    (i.e., as the return type of a method), but not in input positions.

2. [Algerbraic Data Type(ADT) implementation]:
    - a composite type formes by a fixed set of possible type values 
    implementing a standard interface
    - to restrain illegal states
    - to encode enumerated types
    - 2 approaches: the Enumeration class and SEALED traits/abstract classes + case objects (improvement of enumerations)
 
3. [this]:
    - to introduce the current object for a class
    - can refer to instance variables, methods and constructors

4. [modifier final vs sealed]:
    - final: to make a class/trait unavailible for extension
    - sealed: can define its subclasses in the same source file (between public and final)

17. [abstract class]:
    - even if its members don't have abstract modifier, abstract class cannot be instantiated.

5. [apply method in objects]:
    - https://stackoverflow.com/questions/9737352/what-is-the-apply-function-in-scala
    - is essentially the constructors of the class:
        https://alvinalexander.com/scala/how-to-create-scala-object-instances-without-new-apply-case-class/
    
6. [extension]:

7.[control constructions]:
    - if then; else if then; else
    - for generator(s) guard(s) do body
    - for generator(s) guard(s) yield body
    - try ... catch case... finally ...
    - while ... do ...

8. [multiple variables in one line]:
    - val (idx, counter) = (1, 2)

9. [iterable](.foreach):
    - Range(100, 150, 10)

10.[a seq of random numbers]:
    - Seq.fill(10)(Random.nextInt(100)), 10: the number of elements, 100: the upper bound

11. [syntactic sugar]:
    - can use any method that takes a single parameter as binary operator  

12. [partial functions]:
    - functions only work for a subset of possible input values
    - a series of functions only work for a subset of input values
    - often use case statements

13. [scala.io.StdIn.{readInt, readLine} & Console.{RESET,UNDERLINE,RED,BLUE}]:

14. [sort]:
    - import scala.collection.immutable.SortedMap
    - toSeq.sortBy
    - toSeq.sortWith
    - toSeq.sorted

15. [Monads]: see Monads project

16. [copy method in case classes]:
    - automatically generated in case classes
    - to construct new instances of a case class while specifying just the fields that are changing.
    This is very handy for case classes with a lot of fields

18. [a function returns Unit: totally side-effecting]:
    - There’s nothing useful returned from the function, so it can only perform 
    side effects on some state, like performing input or output (I/O). 

19. [pure functions]:
    - have no side effects and return all their work as their return value.
    - easier to reason about, test, compose, and reuse. 
*/

// TODO: Option[class/object]
// TODO: iterable, matchable(sealed trait, abstract class), immutable, sortable
// TODO: nested cases
// TODO: syntactic sugar
// TODO: partial functions
// TODO: Ordering

 /* 1. */
abstract class MyList[T]:
    def head:T
    def tail: MyList[T]
/* 2.1 */
sealed trait CaseObject_Message
case class PlaySong(name: String) extends CaseObject_Message
case class IncreaseVolume(amount:Int) extends CaseObject_Message
case class DecreaseVolume(amount:Int) extends CaseObject_Message
case object StopPlaying extends CaseObject_Message

def handleMessage(message: CaseObject_Message): Unit = message match
    case PlaySong(name) => PlaySong(name)
    case IncreaseVolume(amount) => changeVolume(amount)
    case DecreaseVolume(amount) => changeVolume(-amount)
    case StopPlaying => stopPlayingSong()

def changeVolume(amount:Int): Unit = println(amount)
def stopPlayingSong(): Unit = println("stopping song")
/* 2.2 */
sealed abstract class CurrencyADT(name: String, iso: String)

object CurrencyADT:
    case object EUR extends CurrencyADT("Euro", "EUR")
    case object USD extends CurrencyADT("United States Dollar", "USD")
    val values: Seq[CurrencyADT] = Seq(EUR,USD) // to make ADT iterable
    def fromIso(iso: String): Option[CurrencyADT] = 
        iso.toUpperCase match
            case "USD" => Some(USD)
            case "EUR" => Some(EUR)
            case _ => None
/* 3. */
class Addition(i:Int):
    def this(i:Int, j:Int) = 
        this(i)
        println(i + " + " + j  + " = " + {i+j})
/* 6. */
extension (str:String) def changeBase(radix: Int): Int = Integer.parseInt(str, radix)
/* 16. */
case class Point(x: Double = 0.0, y: Double = 0.0):
  def shift(deltax: Double = 0.0, deltay: Double = 0.0) =
    copy(x + deltax, y + deltay)

@main def test: Unit =
    /* 1. */
    val numList: List[Int] = List(1, 2, 3)
    val head = numList.head
    val tail = numList.tail

    val stringList = List("hello","world", "scala","!")
    val stringListTail = stringList.tail
    println(s"head: $head, tail: $tail")
    println(s"stringListTail: $stringListTail")
    /* 2.1 */
    handleMessage(IncreaseVolume(20))
    /* 2.2 */
    println(CurrencyADT.fromIso("eur"))
    println(CurrencyADT.fromIso("rmb"))
    /* 3.*/
    var sum = new Addition(1,2) // here no print func is needed
    /* 6. */
    println("100".changeBase(8)) // 8^2 = 64
    /* 14. */
    val nameAgeMap = Map(
      ("Jonny", 8),
      ("Tommy", 11),
      ("Cindy", 13),
      ("Bill", 9)
    )
    import scala.collection.immutable.SortedMap
    val sortedNameAge_v1 = SortedMap(
      ("Jonny", 8),
      ("Tommy", 11),
      ("Cindy", 13),
      ("Bill", 9)
    )
    val sortedNameAge_v2a = nameAgeMap.toSeq.sortBy(_._2)(Ordering.Int.reverse).toMap
    val sortedNameAge_v2b = nameAgeMap.toSeq.sortBy(_._1)(Ordering.String.reverse).toMap
    val sortedNameAge_v3 = nameAgeMap.toSeq.sortWith(_._1 > _._1).toMap
    val sortedNameAge_v4 = nameAgeMap.toSeq.sorted.reverse.toMap
    println(sortedNameAge_v2a)
    println(sortedNameAge_v2b)
    println(sortedNameAge_v3)
    println(sortedNameAge_v4)











    
