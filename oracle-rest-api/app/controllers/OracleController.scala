package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import controllers.json.GetAsInt.getAsInt
import play.api.libs.json.JsString
import com.innoq.numbergame.baseapi.Oracle.OracleType._
import controllers.json.UnexpectedJsonException
import play.api.libs.json.Json
import service.OracleService
import scala.collection.immutable.Map

object OracleController extends Controller {

  def newOracle = Action(parse.json) {
	  request => try {
		  if ( ! request.acceptedTypes.exists(_ accepts "application/json")) {
			  BadRequest("You need to accept application/json")
		  } else {
			  val body = request.body
        val base = getAsInt(body, "base", 2)
        val length = getAsInt(body, "length", 2)
        val oracleType = body \ "oracle_type" match {
        case JsString("fair") => FAIR
        case JsString("nice") => NICE
        case JsString("evil") => EVIL
        case _ => throw new UnexpectedJsonException("Need a top-level attribute \"oracle_type\": with value \"fair\" or \"nice\" (or \"evil\")") 
			  }
			  val oracleId = OracleService.newOracle(base, length, oracleType)
				Redirect(controllers.routes.OracleController.oracle(oracleId))
      }
	  } catch {
	  case ex: UnexpectedJsonException => BadRequest(ex.getMessage)
	  case ex: Throwable => InternalServerError(ex.getMessage)
	  }
  }

  def oracle(id : Int) = Action {
    request => try {
      if ( ! request.acceptedTypes.exists(_ accepts "application/json")) {
        BadRequest("You need to accept application/json")
      } else {
        val result = OracleService.get(id).mapValues { any => any match {
          case i:Int => Json.toJson(i)
          case s:String => Json.toJson(s)
          case x => throw new IllegalStateException("Found " + x.getClass().getName())
        }} + ("self" -> Json.toJson(request.uri))
        Ok(Json.toJson(result))
      }
    } catch {
      case ex: Throwable => InternalServerError(ex.getMessage)
    }
  }
}
