package shapes
case class Point(x: Double = 0.0, y: Double = 0.0)
abstract class Shape():
  def draw(f: String => Unit): Unit = f(s"draw: $this")
case class Circle(center: Point, radius: Double) extends Shape
case class Rectangle(lowerLeft: Point, height: Point, width: Point) extends Shape
case class Triangle(p1: Point, p2: Point, p3: Point) extends Shape

sealed trait Message
case class Draw(shape: Shape) extends Message
case class Response(message: String) extends Message
case object Exit extends Message

object ProcessMessages:
  def apply(message: Message): Message =
    message match
      case Draw(shape) =>
        shape.draw(str => println(s"ProcessMessage: $str"))
        Response(s"ProcessMessage: $shape drawn")
      case Response(unexcepted) =>
        val response =Response(s"ERROR: Unexpected Response: $unexcepted")
        println(s"ProcessMessage: $response")
        response
      case Exit =>
        println("ProcessMessage: exiting...")
        Exit
