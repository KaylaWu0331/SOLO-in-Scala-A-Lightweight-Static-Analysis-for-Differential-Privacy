# Notes

## Chapter 2

- Option, Some, None:
    - types that express a very useful concept, when we may or may not have a value.
    - without using Null to avoidNullPointerExceptions
    -  Option: a collection with 0 or one value
- type parameters:
    - e.g., Seq[T] : we can plug any type for T, which iscalled parametric polymorphism -> Expl:Generic implementations of theSeqmethods can be used with instances of any typeT
- Map[K, +V] :
    - +:a variance annotation, covariant typing,Map[K,V2]is a subtype of Map[K, V1]for anyV2that is a subtype ofV
    - without +: K isinvariant. ->We can’t passMap[Any,Any]tof, nor any Map[S, Any]for some subtype or supertypeSofString.

## Chapter 3


- enumerations:
    - While it’s common to declare a type hierarchy to represent all the possible types of some parent abstraction, sometimes we know the list of them is fixed.

## Chapter 4


- Pattern Matching: tomatch expressions, in assignmentstatements, calledpattern bindings
    - allows you to control the visibility of internal state and how to expose it to users.
    - provides deep inspection and decomposition of objects
    - Using | is convenient when two or more cases are handledthe same way.
    - Matches are eager, so more specific clauses must appear before less specific clauses. => So the clauses matching on particular values of types must come before clauses matching on the type (i.e., on any value of the type).
    - Caution!a default case clause:
        - It may indicate that your design could be refined so you know more precisely all the possible matches that might occur
    -  Caution!the lowercase rule:
        -   MatchSurprise.sc
        - a term beginning with a lowercase letter => the name of a variable that will hold a matchedvalue
        - a term beginning with a capital letter => finda definitionalready in scope
        -  conclusion:Incaseclauses, a term that begins witha lowercase letter is assumed to be the name of a new variable that will hold an extracted value. To refer to a previously defined variable, enclose it in backticks or start the name with a capital letter.

- trait with no members: marker trait
- a marker trait **Matchable** :
    - pattern matching can only occur on values of type **Matchable** , not **Any**
    - No unbounded type parameter or abstract type should be decomposable with a pattern match.
- **map** function: (or **foreach** )
    - pass a partial function (MatchVariable.sc), which eliminate the need for x => x match
    - pass an anonymous function (MatchVariable2.sc)
- **Option** type: includes **Some, None**
- How to iterate a Seq using PM and recursion:
    - MatchSeq.sc
    - **head and tail** : methods in Seq, also the variablenames in case clauses
    - **+:** :cons(construction) operator for sequences, also an object named +: in case clauses => symmetric syntax for construction and deconstruction of sequences
- pattern matching in tuples:
    - MatchTuple.sc
    - use tuple literal syntax
    - use ***:** operator
    - The analog of **Nil** for tuples is **EmptyTuple**.
    - the pair construction idiom: a -> b, to create tuples (a,b),which is popular for creatingMapinstances

- case classes:
    - The features of case classes were designed to enable convenient pattern matching.
- define regex:
    - “””...”””.r
    - new instance of the Regex class: Regex(“””...”””)
- assert method:
    - a way to dynamically verify the invariant. The assert method takes a Boolean expression as a parameter and checks it throughout the given code. If the Boolean expression is true, Scala’s assert will execute normally. If false, then Scala’s assert will fail with anAssertionError.
- :@unchecked
    - This compile-time enforcement makes your code more robust, but if you know the declaration is safe, you can add the :@uncheckedtype declaration
    - Seqdoesn’t constrain the number of elements, so thelefthand matches may work or fail. The compiler can’t verify at compile time if the match will succeed or throw aMatchError, so it will report a warning unlessthe@uncheckedtype annotation is added as shown.
- type erasure:
    - the information about the actual types used for the type parameters is not retained in the compiler output.
    - countermeasure: @targetName

## Chapter 5: context abstractions
- context: an extension to a type, a transformation to a new type, or an insertion of values automatically is desired for easier programming
- designed to make the purpose and application of these abstractions more clear and to eliminate some boilerplate when using implicits
- extension:
    - extend a method on class
    - extend a method on object
    - implement an abstraction, s.t., all type extensions are done uniformly -> typeclass
- type classes: (type classes in scala 2 is skipped)
    - can have type-level state across all instances of a type, which can be kept e.g., in a type’s companion object
    - how: declare a trait with a type parameter (this trait defines the abstraction):
    - - this trait includes:
        - extension methods
        -  type-level members, so they are across all instances. For example, protected methods and values, since they are not instance-level members, these protected methods and values are not extension methods
    - given... with...: to declare an instance of a trait, with: implement abstract members in this trait
- [Summ.]: Diff. btw. an instance extension method & a type-level/object method
    - An instance extension method is defined according to specific needs in an instance, i.e., it can be different from instance to instance in the same type;
    - a type-level/object method is a type feature, i.e., it remains the same in different instances of one type.
- Implicit conversions:
    -  Implicit conversions used to be applied in order to implement:
        - “one” -> 1 idiom, replaced by extension
        -  type classes, replaced by extension + given
    -   in scala 3, to convert bet. types for other reasons
    -    Implicit conversions is an optional language feature that we enable by importing scala.language.implicitConversions
    - basic syntax:
        - alias given: given Conversion[-T,+U] = ... (e.g., d =>U(d))
        - given Conversion[-T,+U] with def apply(x: T): U = U(x)
- All uses of underscore:https://www.jianshu.com/p/d271afce8c
    - underscore as numeric literal separator
- Type class derivation:
    - is the idea that we should be able to automatically generate type class given instances as long as they obey a minimum set of requirements, further reducing boilerplate.
-  another mentioned function of Pattern Matching:
    - to scope the visibility of given instances
    - to treat the objects as givens dynamically
- [Summ.] Chap. 5
    - for adding new functionality to existing types without editing their source code

## Chapter 6: Abstracting Over Context: Using Clauses
- using clauses:
    - to work with given instances to address particular design scenarios and to simplify user code
    - to make params in declaration implicit, s.t., users have more flexibility than default values for implementers
- use of context abstractions:
    - to provide method params implicitly rather than explicitly
- keyword using in a method argument list:
    - explicit values for the params are NOT mandatory, AS LONG AS given instances or implicit values are IN SCOPE that thecompiler can use instead.
- using clause:
    - syntax: keyword **using context parameters** :...
    - values for context params, which are in scope: givens
- context parameters
    - syntax: **?=> T**
        - [Ref.](https://stackoverflow.com/questions/69145604/what-is-the-meaning-of-in-scala-3)
    - By-name parameter:
        - syntax: **ParamType :=> Type** , The type of a value parametermay be prefixed by =>
        - [Ref.](https://www.scala-lang.org/files/archive/spec/2.13/04-basic-declarations-and-definitions.html#by-name-parameters)
        - This indicates that the corresponding argument is not evaluated at the point of function application, but instead is evaluated at each use within the function. That is, the argument is evaluated using call-by-name.
        - This is called call-by-name. It means when you call the method, the argument is not evaluated before the method is executed, but rather, it is evaluated each time it is referred to inside the method.
    - Diff btw call-by-value and call-by-name function:
        - [Ref.] (https://www.geeksforgeeks.org/scala-functions-call-by-name/)
        - In Scala when arguments pass through call-by-value function it compute the passed-in expression’s or arguments value _once before calling the function_. But a call-by-Name functionin Scala calls the expression and recompute the passed-in expression’s value every time it get accessed inside the function.
    - Rel. btw. Seq and List;
    - Use **private** to keep the constructor private + define **make method in companion object** => can provide safe constructionof instances:


Squeal: a tip or suggestion

raven: a general note

scorpion: a warning or caution

TODO: universal trait in P258 Value Vlasses; type params in methods; enum ; implicits in
scala (ToJSON example) is skipped

Questions:

- diff. btw. objects and givens
- diff. btw. a function with using clause & with type params
- =>, =, ->
- diff. btw. method and function definition
- with or without override