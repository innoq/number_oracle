package service

import java.io.ByteArrayInputStream
import java.sql.PreparedStatement
import scala.collection.immutable.Map
import scala.collection.immutable.TreeMap
import com.innoq.numbergame.base.OracleFactory
import com.innoq.numbergame.baseapi.Oracle.OracleType
import com.innoq.numbergame.baseapi.Oracle.OracleType.FAIR
import com.innoq.numbergame.serialize.FixedCodeOracleSerializer
import anorm.NamedParameter.symbol
import anorm.SQL
import anorm.ToStatement
import anorm.sqlToSimple
import play.api.Play.current
import play.api.db.DB
import anorm.ResultSetParser
import java.util.Locale
import javax.sql.rowset.serial.SerialBlob
import com.innoq.numbergame.baseapi.BadAttemptException
import java.sql.Date
import org.joda.time.Instant
import anorm.SqlQuery

object OracleService {
     
  // I should not need to do this:
  implicit object byteArrayToStatement extends ToStatement[Array[Byte]] {
    def set(ps: PreparedStatement, i: Int, array: Array[Byte]): Unit = {
      ps.setBytes(i, array)
    }
  }

  def newOracle(base: Int, length: Int, oracleType: OracleType) : Long = {
    oracleType match {
      case OracleType.FAIR =>  {
        val state = 
        		FixedCodeOracleSerializer.marshal(OracleFactory.makeRandomFairOracle(length, base))
        DB.withTransaction( implicit connection => {
          SQL("INSERT INTO oracle(type, base, length, solved, deserializer, state) " + 
              "values('fair', {base}, {length}, FALSE, {deserializer}, {state})").
          on('base -> base, 'length -> length,
              'deserializer -> "FixedCodeOracleSerializer", 'state -> state).executeInsert()
        }) match {
          case Some(i) => {System.err.println("Habe record id " + i); i}
          case None => throw new IllegalStateException("No record id after database insert.")
        }
      }
      
      case OracleType.NICE => {
        // Need no status for NICE oracles, only the solved flag.
        DB.withTransaction { implicit connection => {
          SQL("INSERT INTO oracle(type, base, length, solved, deserializer) " + 
              "values('nice', {base}, {length}, FALSE, {deserializer})").
          on('base -> base, 'length -> length,
              'deserializer -> "implicit").executeInsert()
        }} match {
          case Some(i) => i
          case None => throw new IllegalStateException("No record id after database insert.")
        }
      }
      case OracleType.EVIL => throw new IllegalStateException("Evil oracles not yet implemented, sorry.")
    }
  }

  def get(id: Long) : Option[Map[String, Any]] = {
		  DB.withTransaction { implicit connection => {
			  SQL(""" SELECT
                   type, base, length, solved, attempts, first_guess, solving_guess
                FROM oracle
                WHERE id = {id}""").on('id -> id).apply()
			  .map(row => {
          val solved = row[Boolean]("solved")
          val baseResult = 
				  TreeMap("base" -> row[Int]("base"),
						  "length" -> row[Int]("length"),
              "attempts" -> row[Int]("attempts"),
              "solved" -> solved,
						  "type" -> OracleType.valueOf(row[String]("type").toUpperCase(Locale.ROOT)))
				  if(row[Boolean]("solved")) {
            baseResult + ("solved" -> solved)
          } else {
            baseResult
          }
			  })
		  }}.headOption
  }
  
    def divinate(id: Long, submission: Array[Int]) : Option[Map[String, Any]] = {
      DB.withTransaction { implicit connection => {
        SQL("""SELECT
        		     type, solved, attempts, first_guess, base, length, deserializer, state
               FROM oracle
               WHERE id = {id}
               FOR UPDATE""").on('id -> id)
        .apply()
        .map(row => {
          val otype = OracleType.valueOf(row[String]("type").toUpperCase(Locale.ROOT))
          val length = row[Int]("length")
          if(submission.length != length)
        	  throw new BadAttemptException("You need to submit " + length + " digits rather than " + submission.length)
          val base = row[Int]("base")
          submission.foreach(digit => {
        	  if(digit < 0 || base <= digit)
              throw new BadAttemptException("You need to submit digits within [" + 0 + ", " + base + ") which excludes " + digit)
          })
          if(row[Boolean]("solved")) {
        	  throw new BadAttemptException("The game instance at id " + id + " has already been solved, don't bother me again.")
          }
          val attempts = row[Int]("attempts")
          val deserializer = row[String]("deserializer")
          val result = if("implicit".equals(deserializer)) {
              TreeMap("full_match_count" -> length, "partial_match_count" -> 0)
          } else {
        	  val oracle = deserializer match {
            case "FixedCodeOracleSerializer" => {
              System.err.println("Going for state.")
              val state = row[Array[Byte]]("state")
              System.err.println("Have state: " + state.getClass().getName())
            	FixedCodeOracleSerializer.unmarshal(state)
            }
            case whatsit => 
              throw new IllegalStateException("Oracle database record at " + id + " has unsupported deserializer \"" + deserializer + "\"")
            }
            val r = oracle.divinate(submission)
            TreeMap(
              "full_match_count" -> r.getFullMatchCount, 
              "partial_match_count" -> r.getPartialMatchCount)
          }
          System.err.println("Have tree map")
          val update: SqlQuery = 
            if(result("full_match_count") == length) {
            	// They solved the oracle
            	System.err.println("Solved!")
            	row.apply[Option[Instant]]("first_guess") match {
            	case None => SQL("UPDATE oracle SET first_guess = now(), solved = TRUE, solving_guess = first_guess, attempts = {attempts} WHERE id = {id}")
            	case _ =>    SQL("UPDATE oracle SET solved = TRUE, solving_guess = now(), attempts = {attempts} WHERE id = {id}")
            	}
            } else {
            	System.err.println("Not solved!")
            	row.apply[Option[Instant]]("first_guess") match {
            	case None => SQL("UPDATE oracle SET first_guess = now(), attempts = {attempts} WHERE id = {id}")
            	case _ => SQL("UPDATE oracle SET attempts = {attempts} WHERE id = {id}")
            	}
            }
          System.err.println("Have update statement map")
          update.on('id -> id, 'attempts -> (attempts + 1)).executeUpdate()
          result
        })
      }}.headOption
  }
}
