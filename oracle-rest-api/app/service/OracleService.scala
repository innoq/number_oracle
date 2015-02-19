package service

import com.innoq.numbergame.baseapi.Oracle
import com.innoq.numbergame.baseapi.Oracle.OracleType
import com.innoq.numbergame.baseapi.Oracle.OracleType._
import scala.collection.immutable.Map
import scala.collection.immutable.TreeMap
import com.innoq.numbergame.base.OracleFactory

object OracleService {

  def newOracle(base: Int, length: Int, oracleType: OracleType) : Int = {
    val oracle =  oracleType match {
      case OracleType.FAIR => OracleFactory.makeRandomFairOracle(length, base)
      case OracleType.NICE => OracleFactory.makeNiceOracle(length, base)
      case OracleType.EVIL => throw new IllegalStateException("Evil oracles not yet implemented, sorry.")
    }
    7
  }

  def get(id: Int) : Map[String, Any] = {
    TreeMap("base" -> 3, "length" -> 3, "oracle_type" -> FAIR.toLcString())
  }
}
