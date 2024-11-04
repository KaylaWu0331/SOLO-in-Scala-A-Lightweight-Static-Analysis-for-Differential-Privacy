/*convert implicit parameter to function literals in scala to context function in scala 3
* Description:
* - to run simple code blocks asynchronously
* */
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.*
val sameThreadExecutionContext = new ExecutionContext:
  // to demonstrate replacing the use of the global implicit value with one we control in scoped contexts.
  def execute(runnable: Runnable): Unit =
    println("start > ")
    runnable.run()
    println("finish > ")
  def reportFailure(cause: Throwable): Unit =
    println(s"sameThreadExecutionContext failure: $cause")
object AsyncRunner2: // in scala 2
  def apply[T](body: ExecutionContext => Future[T]): T =
    // body: a block takes an ExecutionContext argument and returns a Future.AsynRunner2.apply()
    val future = body(sameThreadExecutionContext) // pass the custom ExecutionContext
    Await.result(future, 2.second) // waits up 2 seconds for the results (arbitrary)
@main def testResult2() =
  val result2 = AsyncRunner2(implicit executionContext => Future(1).map(_ * 2).filter(_ > 0))
  println(result2)

object AsyncRunner3: // in scala 3
  type RunnerContext[T] = ExecutionContext ?=> Future[T]
  def apply[T](body: => RunnerContext[T]): T =
    given ExecutionContext = sameThreadExecutionContext
    Await.result(body, 2.seconds)
@main def testResult3() =
  val result3 = AsyncRunner3(Future(1).map(_ * 2).filter(_ > 0))

