package com.cinglevue.challenge

import org.scalatra.json.JacksonJsonSupport
import org.json4s.DefaultFormats
import com.cinglevue.challenge.model._
import com.cinglevue.challenge.model.Codecs._
import reactivemongo.api.DB
import reactivemongo.bson._
import org.scalatra.{AsyncResult, FutureSupport}
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext

class Schools(db:DB) extends CinglevueCodingChallenceStack with JacksonJsonSupport with FutureSupport {
  // This is a ugly, but helps reduce the number of ActorSystem objects in our ecosystem.
  protected implicit def executor: ExecutionContext = db.connection.actorSystem.dispatcher

  val log = LoggerFactory.getLogger(this.getClass)
  protected implicit val jsonFormats = DefaultFormats.withBigDecimal
  get("/") {
    redirect(url("index"))
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }

  get("/list/:subject") {
    new AsyncResult() {
      val is = {
        contentType = formats("json")
        val collection = db.collection("Schools")
        // An empty Query document - we want ALL records
        val query = BSONDocument()
        val projection = BSONDocument("subjects" -> BSONDocument("$elemMatch"->BSONDocument("name" -> BSONRegex(params("subject"),"i"))), "name"->1)
        val cursor = collection.find(query, projection).cursor[School]
        val futureResultSet = cursor.toList
        // Simplyfy the object model for presentation:
        val futureViewModel =  futureResultSet.map(_.map(buildView))
        // Every once in a while, due to network hiccup, reactive throws an exception.
        // TOOD: Improve error handling...
        futureResultSet.onFailure {
          case e => log.warn("Error deserializing Schools", e)
        }
        futureViewModel
      }
    }
  }

  private def buildView(s:School) = {
    case class SubjectView(name: String, results: Map[String, Long])
    def normalizeResults(r: Result) = {
      Map(r.year.toString -> r.score) //Can't have a numeric Field
    }
    def normalizeSubjects(sub: Subject) = {
      import collection.mutable.{Map => MutableMap}
      val results:MutableMap[String,Long] = sub.results.map(normalizeResults).foldLeft(MutableMap[String, Long]()){(s,v) =>
        s ++= v
      }
      Map ("subject" -> sub.name, "results" -> results.toMap)
    }
    Map("name" -> s.name, "subjects" -> s.subjects.map(normalizeSubjects))
  }
}
