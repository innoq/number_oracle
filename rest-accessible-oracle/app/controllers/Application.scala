package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import views.html.defaultpages.badRequest
import scala.util.parsing.json.JSONObject
import play.api.libs.json.JsObject
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber
import play.api.libs.json.JsValue
import play.api.http.MediaRange
import play.api.libs.json.JsString

object Application extends Controller {

    private class UnexpectedJsonException(mes: String) extends Exception(mes) {    
    }

    private def getAsInt(v: JsValue, name: String, minValue: Int) = {
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

    def index = Action {
        Ok(views.html.index("Your new application is ready."))
    }

    def newgame = Action(parse.json) {
        request => try {
            if ( ! request.acceptedTypes.exists(_ accepts "application/json"))
                BadRequest("You need to accept application/json")
            else {
                    val body = request.body
                val base = getAsInt(body, "base", 2)
          val length = getAsInt(body, "length", 2)
          val oracle_type = body \ "oracle_type" match {
            case JsString("fair") => "fair"
            case JsString("nice") => "nice"
            case JsString("evil") => "evil"
            case _ => throw new UnexpectedJsonException("Need a top-level attribute \"oracle_type\": with value \"fair\" (or \"nice\" or \"evil\")") 
          }
                    val result = Json.obj("base_found" -> base, "length_found" -> length, "oracle_type_found" -> oracle_type)
                    Ok(result)
      }
        } catch {
        case ex: UnexpectedJsonException => BadRequest(ex.getMessage)
        case ex: Throwable => InternalServerError(ex.getMessage)
        }
    }
}
