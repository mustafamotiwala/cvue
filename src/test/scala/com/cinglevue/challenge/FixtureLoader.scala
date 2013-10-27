package com.cinglevue.challenge
import org.json4s._
import org.json4s.jackson.JsonMethods._
import java.io.File
import com.cinglevue.challenge.model.School

/**
 * Strictly speaking, this is not a test. Well okay, this is not even remotely meant to be a test.
 * This is a script/app to load sample data from JSON to Mongo.
 * User: mabdullah
 * Date: 10/27/13
 * Time: 11:01 PM
 * To change this template use File | Settings | File Templates.
 */
object FixtureLoader {
  def main(args:Array[String]) {
    val fixtureFile = this.getClass.getResource("/fixture-data.json")
    implicit val formats = DefaultFormats
    val schools = parse(FileInput(new File(fixtureFile.toURI))).extract[List[School]]
    //TODO: Push to MongoHQ
  }
}
