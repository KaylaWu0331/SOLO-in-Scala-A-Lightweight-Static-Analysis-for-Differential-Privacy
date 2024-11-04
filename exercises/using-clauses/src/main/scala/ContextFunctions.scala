/* Context Functions:
- functions with context parameters only.
- Ways to invoke CF and regular functions are different.
*/
//FIXME 20:07 2023/2/5: still need to figure out the usage of context functions
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
object FutureCF:
  // a wrapper to handle the ExecutionContext passed to Future.apply()
  type Executable[T] = ExecutionContext ?=> T
  // highlight,NO SPACE btw ? and =
  // Type member alias for a context function with an ExecutionContext.
  // ?=> In context function types, separates the parameter list from the body.
  def apply[T](body: => T): Executable[Future[T]] = Future(body) // do have A SPACE btw : and = //2
def sleepN(dur: Duration): Duration = // will be passed to futures
  val start = System.currentTimeMillis()
  Thread.sleep(dur.toMillis)
  Duration(System.currentTimeMillis - start, MILLISECONDS)
val future1 = FutureCF(sleepN(1.second)) // implicit ExecutionContext
//- When FutureCF.apply(sleepN(1.second)) is called:
  // - it returns Executable(Future(sleepN(1.second))), which is the same as (given ExecutionContext) ?=> Future(sleepN(1.second))
  // - convert Executable(Future(SleepN(1.second))) to Future(sleepN(1.second))(given ExecutionContext)
  // - invoke the converted term to return the Future
val future2 = FutureCF(sleepN(1.second))(using global) // explicit ExecutionContext
val duration1 = Await.result(future1, 2.second) // Await the results of the futures. Wait no longer than two seconds.
val duration2 = Await.result(future2, 2.second)
@main def test =
  println(future1)
  println(future1.getClass)
  println(duration1)
  println(duration1.getClass)
  println(duration2)

//Future(Success(1006 milliseconds))
//class scala.concurrent.impl.Promise$Transformation
//1006 milliseconds
//class scala.concurrent.duration.FiniteDuration
//1006 milliseconds

