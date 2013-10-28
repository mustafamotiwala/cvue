package com.cinglevue.challenge

import org.scalatra.json.JacksonJsonSupport
import org.json4s.DefaultFormats
import com.cinglevue.challenge.model.School
import com.cinglevue.challenge.model.Codecs._
import reactivemongo.api.DB
import reactivemongo.bson._
import org.scalatra.{AsyncResult, FutureSupport}
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext


class Schools(db:DB) extends CinglevueCodingChallenceStack with JacksonJsonSupport with FutureSupport{
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
        val query = BSONDocument()
        val filter = BSONDocument("subjects" -> BSONDocument("$elemMatch"->BSONDocument("name" -> BSONRegex(params("subject"),"i"))), "name"->1, "_id"->0)
        val cursor = collection.find(query, filter).cursor[School]
        val future = cursor.toList
        future.map(_.map(x => log.info(s"Result: ${x}")))
        future.onFailure {
          case e => log.warn("Error deserializing Schools", e)
        }
        future
      }
    }
  }
}
