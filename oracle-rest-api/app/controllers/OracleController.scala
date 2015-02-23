package controllers

import scala.annotation.implicitNotFound
import com.innoq.numbergame.baseapi.Oracle.OracleType.EVIL
import com.innoq.numbergame.baseapi.Oracle.OracleType.FAIR
import com.innoq.numbergame.baseapi.Oracle.OracleType.NICE
import controllers.json.GetAsInt.getAsArray
import controllers.json.GetAsInt.getAsInt
import controllers.json.UnexpectedJsonException
import json.MapToJsonResult.mapToJsonResult
import play.api.libs.json.JsString
import play.api.mvc.Action
import play.api.mvc.Controller
import service.OracleService
import com.innoq.numbergame.baseapi.BadAttemptException

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
        case _ => throw new UnexpectedJsonException("Need a top-level attribute \"oracle_type\": with value \"fair\" or \"nice\" or \"evil\"") 
			  }
			  val oracleId = OracleService.newOracle(base, length, oracleType)
				Redirect(controllers.routes.OracleController.oracle(oracleId))
      }
	  } catch {
	  case ex: UnexpectedJsonException => BadRequest(ex.getMessage)
	  case ex: Throwable => InternalServerError(ex.getMessage)
	  }
  }

  def oracle(id : Long) = Action {
    request => try {
      if ( ! request.acceptedTypes.exists(_ accepts "application/json")) {
        BadRequest("You need to accept application/json")
      } else {
        OracleService.get(id) match {
          case Some(map) => mapToJsonResult(map, Some(request.uri), this)
          case None => NotFound("No oracle with id " + id + " in database.")  
        }
      }
    } catch {
      case ex: Throwable => InternalServerError(ex.getMessage)
    }
  }

  def divinate(id : Long) = Action(parse.json) {(
    request => try {
      if ( ! request.acceptedTypes.exists(_ accepts "application/json")) {
        BadRequest("You need to accept application/json")
      } else {
        val body = request.body
        val submission = getAsArray(body, "submission")
        OracleService.divinate(id, submission) match {
          case Some(map) => mapToJsonResult(map, None, this)
          case None => NotFound("No oracle with id " + id + " in database.")
        }
      }
    } catch {
    case ex: UnexpectedJsonException => BadRequest(ex.getMessage)
    case ex: BadAttemptException => BadRequest(ex.getMessage)
    case ex: Throwable => InternalServerError(ex.getMessage)
    })
  }
}
