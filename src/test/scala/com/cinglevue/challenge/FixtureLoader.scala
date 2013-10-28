package com.cinglevue.challenge
import org.json4s._
import org.json4s.jackson.JsonMethods._
import java.io.File
import com.cinglevue.challenge.model._
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api._
import reactivemongo.core.actors.Authenticate
import reactivemongo.bson._
import scala.concurrent.Await
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import reactivemongo.core.commands.SuccessfulAuthentication
import org.slf4j.LoggerFactory

/**
 * Strictly speaking, this is not a test. Well okay, this is not even remotely meant to be a test.
 * This is a script/app to load sample data from JSON to Mongo.
 * User: mabdullah
 * Date: 10/27/13
 * Time: 11:01 PM
 * To change this template use File | Settings | File Templates.
 */
object FixtureLoader {
  val log = LoggerFactory.getLogger(this.getClass)
  def main(args:Array[String]) {
    val fixtureFile = this.getClass.getResource("/fixture-data.json")
    implicit val formats = DefaultFormats
    val config = ConfigFactory.load
    val dbServer = s"""${config getString "com.cinglevue.challenge.db.host"}:${config getString "com.cinglevue.challenge.db.port"}"""
    val dbName = config getString "com.cinglevue.challenge.db.name"
    val dbUsername = config getString "com.cinglevue.challenge.db.user"
    val dbPassword = config getString "com.cinglevue.challenge.db.password"
    val schools = parse(FileInput(new File(fixtureFile.toURI))).extract[List[School]]
    log.debug(s"Connecting to: ${dbServer}/${dbName}")

    val driver = new MongoDriver
    val auth = new Authenticate(dbName, dbUsername, dbPassword)
    val connection = driver.connection(List(dbServer))
    val authTimeout = 5.seconds
    val authFuture = connection.authenticate(dbName, dbUsername, dbPassword)(authTimeout)
    //Possible bug in ReactiveMongo: The future completes when even a single channel authenticates.
    // Need to explore further
    authFuture.onComplete(_ match{
      case s:Success[SuccessfulAuthentication] => log.info("Authentication successful")
      case x:Failure[SuccessfulAuthentication] => log.error("Authentication failed: " + x.exception.getMessage)
        x.exception.printStackTrace()
    })
    Await.result(authFuture, authTimeout)

    //Need to add a delay as all channels may not have finished authenticating
    driver.system.scheduler.scheduleOnce(10.seconds) {
//      insertDocuments(connection(dbName),schools)
      queryDocuments(connection(dbName))
    }
    driver.system.scheduler.scheduleOnce(30.seconds) {
      log.info("Shutting down the reactor...")
      driver.close()
      //Definite bug; some of the threads from the AS linger on preventing termination.
    }
  }

  def insertDocuments(db:DB, schools:Seq[School]) = {
    val collection = db.collection("Schools")
    log.debug(s"Total Number of schools to insert: ${schools.size}")
    log.debug("Printing List of Schools:")
    schools.foreach(s=>log.debug(s"${s.name}"))
    val insertFutures = schools.map{s =>
      log.info(s"Queuing insert for school: ${s.name}")
      collection.insert(s)
    }
    insertFutures.foreach{f =>
      f.onSuccess {
        case _ => log.info("Record inserted successfully")
      }
      f.onFailure {
        case e => log.warn("Error inserting record:",e)
      }
    }
  }

  def queryDocuments(db:DB) = {
    val collection = db.collection("Schools")
    val query = BSONDocument()
    val cursor = collection.find(query).cursor[School]
    val f = cursor.toList
    f.map(_.map( x => log.debug(s"ResultDocument: ${x}")))
  }
}
