package controllers.json

import play.api.libs.json.JsValue
import play.api.libs.json.JsNumber

object GetAsInt {
  
  def getAsInt(v: JsValue, name: String, minValue: Int) = {
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
}