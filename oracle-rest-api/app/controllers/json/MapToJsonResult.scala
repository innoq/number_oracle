package controllers.json

import scala.collection.immutable.Map

import com.innoq.numbergame.baseapi.Oracle.OracleType

import play.api.libs.json.Json
import play.api.mvc.Controller
import play.api.mvc.Result

object MapToJsonResult {

  def mapToJsonResult(map: Map[String, Any], selfUri: Option[String], controller: Controller) : Result = {
      controller.Ok(
        Json.toJson {
          val jsonMap = map.mapValues {
            any => any match {
            case i:Int => Json.toJson(i)
            case d:Double => Json.toJson(d)
            case s:String => Json.toJson(s)
            case b: java.lang.Boolean => Json.toJson(b.booleanValue())
            case t:OracleType => Json.toJson(t.toLcString())
            case x => throw new IllegalStateException("Found in mapToJsonResult: " + x.getClass().getName())
            }
          }
          selfUri match {
            case None => jsonMap
            case Some(uri) => jsonMap + ("self" -> Json.toJson(uri))
          }
        }
      )
  }
}
