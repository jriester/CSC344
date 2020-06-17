//James Riester, CSC344, Professor Schlegel, Assignment 3 : Scala

import scala.io.StdIn.readLine

abstract class Tree

case class S(E: E, EOF: Char) extends Tree
case class E(T: T, E2: E2) extends Tree
case class E2(Alt: Char, E3: E3) extends Tree
case class E3(T: T, E2: E2) extends Tree
case class T(F: F, T2: T2) extends Tree
case class T2(F: F, T2: T2) extends Tree
case class F(A: A, F2: F2) extends Tree
case class F2(Opt: Char, F2: F2) extends Tree
case class A(C: Char, A2: A2) extends Tree
case class A2(E: E, C: Char) extends Tree

object Main {

  def main(args: Array[String]) {

    val userPattern: String = scala.io.StdIn.readLine("pattern? ")
    val parser = new RecursiveDecent(userPattern, 0)
    val start = parser.parse
    var userInput = scala.io.StdIn.readLine("string? ")

    while (userInput != "quit") {
      val found: Boolean = eval(userInput, start, 0)
      if (found) {
        println("match")
      }	else println("no match")
      userInput = readLine("string? ")
    }
  }

//--------------------------------------------------------------------------------------------------------------------------------------------

  def eval(string: String, tree: S, cur: Int): Boolean = {
    val userStr: String = string
    var found: Boolean = false
    var flag: Boolean = true
    val pattern: S = tree
    var f2flag: Boolean = false
    var a2flag: Boolean = false
    var curr = cur; var last: Int = 0
    //var dotbool: Boolean = false

    def navigate(t: Any): Any = t match {

      case t:S => navigate(t.E)

      case t:E => navigate(t.T)
        if (flag == true) {
          //quit, already found.
        }
        if (t.E2 != null && flag==false) {
          flag = true
          curr = last
          navigate(t.E2)
        }

      case t:T => navigate(t.F)
        if (t.T2 != null) {
          navigate(t.T2)
        }
      case t:F =>
        if (t.F2 != null) {
          f2flag = true
        }
        navigate(t.A)

      case t:A =>
        //print("t.C " + t.C + "\n") //this is input expression
        //print("userStr: " + userStr.charAt(curr) + "\n") //userString at current index. pushed forward by
        if (t.C == userStr.charAt(curr)) {
          curr += 1
          found = true
          //if expr = userString(index), good, move forward.
          if (!a2flag) f2flag = false
          //a2 is end of tree, have been there then reset the f2 flag so we can traverse again

        } else if (t.C == '(') {
          last = curr
          if (f2flag == true) {
            a2flag = true;
          }
          navigate(t.A2)
          //see a '(' then mark the index, since we have '(' we need a ')' so set a2 to true
        } else if (t.C == '.') {
          curr += 1
          found = true
          //see '.' (any char), then don't try to match it, just move forward
        } else if (t.C != userStr.charAt(curr) && f2flag == true) {
          found = true
          //deals with optional, if they aren't equal but we've been to f2('?') then just let anything pass.
          if (!a2flag) f2flag = false
          //reset flag as above

        } else flag = false

      case t:A2 => navigate(t.E)
        a2flag = false; f2flag = false
        //resetting flags again

      case t:T2 => navigate(t.F)
        if (t.T2 != null) {
          navigate(t.T2)
        }

      case t:E2 => navigate(t.E3)

      case t:E3 => navigate(t.T)
        if (flag == true) {
          //end traversal
        } else if (t.E2 != null) {
          flag = true
          navigate(t.E2)
        }
    }
    navigate(pattern)


    //checks if program thinks it's done but haven't gotten to end yet.
    if (flag == true && curr < userStr.length()-1) {
      flag = false;
    }

    //can't find if we haven't gone through string yet
    if (flag == false) {
      found = false
    }
    curr = 0
    found
  }
}

//---------------------------------------------------------------------------------------------------------------------

class RecursiveDecent(inp: String, curren: Int) {
  var curr: Int = curren;
  var input: String = inp;

  //move forward
  def forward() = {
    curr = curr + 1
  }
  //look at input at previous index
  def prev(): Char = {
    input.charAt(curr - 1)
  }
  //current index of string
  def current(): Char = {
    //kept getting java.lang.StringIndexOutOfBoundsExceptions, if the next index is the last one then EOF
    if ((curr + 1) == input.length()) {
      '$'
    } else {
      input.charAt(curr)
    }
  }

  /*
    S  -: E$
    E  -: T E2
    E2 -: '|' E3
    E2 -: NIL
    E3 -: T E2
    T  -: F T2
    T2 -: F T2
    T2 -: NIL
    F  -: A F2
    F2 -: '?' F2
    F2 -: NIL
    A  -: C
    A  -: '(' A2
    A2 -: E ')'
  */

  def parse(): S = {
    // parsing starts here
    if (input=="$") {
      // for null pattern inputs
      S(E(T(F(A('\0',null),null),
        null),null),'$')
    }    else parseS()
  }

  def parseS(): S = {
    S(parseE: E, '$')
  }

  def parseE(): E = {
    E(parseT, parseE2)
  }

  def parseE2(): E2 = {
    //hardest part, dealing with separating the regex input.
    //If we see the '|' and the prev is ')' then the previous statement stands alone
    //EX: ((h|j)ell. worl?d)|(42)                This separates the "hello world" and "42"

    if (current == '|' && prev != ')') {
      if (curr < input.length() - 1) {
        forward()
      }
      E2('|', parseE3)
    } else if (prev == ')' && current == '|' && curr < input.length() - 1) {
      input = input.substring(0, curr) + '|' + input.substring(curr, input.length())
      if (curr < input.length() - 1) {
        forward()
      }
      null
    } else if (current == '|' && prev == '|') {
      null
    } else null
  }

  def parseE3(): E3 = {
    E3(parseT, parseE2)
  }
  def parseT(): T = {
    if (current != ')') {
      T(parseF, parseT2)
    } else null
  }

  def parseT2(): T2 = {
    //if no forbidden chars, recursive call to F(accesses A for chars) and T2, loops to parse chars
    if (current != '|' && current != ')' && current != '$'
      && current != '?' && curr <= input.length()) {
      T2(parseF, parseT2)
    } else if (current == ')') {
      if (curr <= input.length()) {
        forward()
      }
      null
    } else null
  }
  def parseF(): F = {
    F(parseA, parseF2)
  }

  def parseF2(): F2 = {
    //handles optional, if we see optional move forward and recursive call to F2 with '?' added
    if (current == '?') {
      if (curr <= input.length()) {
        forward()
      }
      F2('?', parseF2)
    } else null
  }

  def parseA(): A = {
    if (current != '?' && current != '|' && current != ')' && current == '(') {
      A('(', parseA2)
      //If '(', go to A2 like in tree to match with corresponding ')'

    } else if (current != '?' && current != '|'&& current != ')' && current != '(') {
      if (current == '.') {
        //print("found dot in parse\n")
        forward()

      } else if (curr <= input.length()) {
        //anything that isn't ()|?
        forward()
      }
      A(prev, null)
      //recursive call to A again to keep parsing inside given parameters
    }
    else null
  }

  def parseA2(): A2 = {
    if (curr <= input.length()) {
      forward ()
    }
    A2(parseE, ')')
  }
}
