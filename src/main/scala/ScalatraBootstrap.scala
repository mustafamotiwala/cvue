import com.cinglevue.challenge._
import com.typesafe.config.ConfigFactory
import org.scalatra._
import javax.servlet.ServletContext
import org.slf4j.LoggerFactory
import reactivemongo.api.MongoDriver
import reactivemongo.core.nodeset.Authenticate
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.core.commands.SuccessfulAuthentication
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class ScalatraBootstrap extends LifeCycle {
  val log = LoggerFactory.getLogger(this.getClass)
  override def init(context: ServletContext) {
    val config = ConfigFactory.load
    val dbServer = s"""${config getString "com.cinglevue.challenge.db.host"}:${config getString "com.cinglevue.challenge.db.port"}"""
    val dbName = config getString "com.cinglevue.challenge.db.name"
    val dbUsername = config getString "com.cinglevue.challenge.db.user"
    val dbPassword = config getString "com.cinglevue.challenge.db.password"
    val credentials = List(Authenticate(dbName, dbUsername, dbPassword))

    val driver = new MongoDriver
    val connection = driver.connection(List(dbServer), authentications = credentials)
    // val authTimeout = 5.seconds
    // val authFuture = connection.authenticate(dbName, dbUsername, dbPassword)(authTimeout)
    // Possible bug in ReactiveMongo: The future completes when even a single channel authenticates.
    // Need to explore further
    // authFuture.onComplete( _ match {
    //   case s:Success[SuccessfulAuthentication] => log.info("Authentication successful")
    //   case x:Failure[SuccessfulAuthentication] => log.error("Authentication failed: ", x.exception)
    // })
    // Await.result(authFuture, authTimeout)
    context mount(new Schools(connection(dbName)), "/")
    log info "Servlet Schools mounted on /"
  }
}
