/*
[typeclass pattern in scala 3]:
    - a trait with a type parameter, any extension (instance) methods, and type-level members
*/
package json

trait ToJSON[T]:
    extension (t: T) def toJSON(name: String ="", level: Int = 0): String  // instance method
    protected val indent = "    "
    protected def indentation(level: Int): (String, String) =
        (indent * level, indent * (level + 1))
    protected def handleName(name: String): String =      // the rest are implementation details, type-level
        if name.length > 0 then s""""$name:":""" else ""

