/* by-name context parameters:
- can provide the benefit of delayed evaluation until it is actually used
- here is used in doTransaction method declaration, s.t.,
- the given instance ConnectionManager isn't created until doTransaction is called
*
* */
type Status = String
case class Transaction(database: String):
  def begin(query: String): Status = s"$database: Starting transaction: $query"
  def rollback(): Status = s"$database: Rolling back transaction"
  def commit(): Status = s"$database: Committing transaction"
case class ConnectionManager(database: String):
  // A connection manager, intended to be expensive to create, so it’s better to delay
  //construction until needed.
  println(s"... expensive initialization for database $database")
  def createTransaction: Transaction = Transaction(database)
def doTransaction(query: => String)(using cm: => ConnectionManager): Seq[Status] =
  //(using cm: => ConnectionManager) a by-name parameter in this using clause
  val trans = cm.createTransaction
  Seq(trans.begin(query), trans.commit())
@main def doPostgreSQL =
  println("Start of doPostgreSQL.")
  given ConnectionManager = ConnectionManager("PostgreSQL") // database = "PostgreSQL"
  println("Start of doTransaction.") // the output order of line 23 amd 24 is reversed, hence delayed construction
  println(doTransaction("SELECT * FROM table"))

// Start of doPostgreSQL.
//Start of doTransaction.
//... expensive initialization for database PostgreSQL
//List(PostgreSQL: Starting transaction: SELECT * FROM table, PostgreSQL: Committing transaction)

