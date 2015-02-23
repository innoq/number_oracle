package controllers.json

import play.api.libs.json.JsValue
import play.api.libs.json.JsNumber
import play.api.libs.json.JsArray

object GetAsInt {
  
  def getAsInt(v: JsValue, name: String, minValue: Int) : Int = {
        v \ name match {
        case JsNumber(someNumber) =>
          if(someNumber.isValidInt) {
              val i = someNumber.toIntExact
              if (minValue <= i)
                i
              else
                throw new UnexpectedJsonException("\"" + name + "\": " + i + " makes no sense.")
          } else {
            throw new UnexpectedJsonException("Found \"" + name + "\":, it's a number, but value needs to be an integer.")
          }
        case _ => throw new UnexpectedJsonException("Expected \"" + name + "\": <some integer>")
        }
    }
  
  def getAsArray(v: JsValue, name: String) : Array[Int] = {
    v \ name match {
      case JsArray(seq: Seq[JsValue]) => {
    	  (seq.map { jsvalue => jsvalue match {
          case JsNumber(someNumber) => 
            if(someNumber.isValidInt)
              someNumber.toIntExact
            else
              throw new UnexpectedJsonException("Expected integers only in array, found " + someNumber)
          case _ => throw new UnexpectedJsonException("Expected integers only in array, found " + jsvalue.toString())
    	    }
        }).toArray
      }
      case _ => throw new UnexpectedJsonException("Expected \"" + name + "\": [ <integers> ]")
    }
  }
}
