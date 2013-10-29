package com.cinglevue.challenge.model

import reactivemongo.bson._
import org.slf4j.LoggerFactory

/**
 * Created with IntelliJ IDEA.
 * User: mabdullah
 * Date: 10/27/13
 * Time: 9:11 PM
 * To change this template use File | Settings | File Templates.
 */
case class School (_id: Option[BSONObjectID], name: String, subjects:List[Subject]){
  def toBSON = {
    BSONDocument("name"->name, "subjects"->BSONArray(subjects.map(_.toBSON)))
  }
}
case class Subject(_id:Option[BSONObjectID], name:String, results:List[Result]){
  def toBSON = {
    BSONDocument("name"->name,"results"->BSONArray(results.map(_.toBSON)))
  }
}
case class Result(_id:Option[BSONObjectID], year:Int, score:Long){
  def toBSON = {
    BSONDocument("year"->year, "score"->score)
  }
}

object Codecs{
  private val log = LoggerFactory.getLogger(this.getClass)
  implicit object SchoolBSONCodec extends BSONDocumentReader[School] with BSONDocumentWriter[School] {
    def write(t: School):BSONDocument = t.toBSON

    def read(bson: BSONDocument):School = {
      val bsonArray = bson.getAs[BSONArray]("subjects") getOrElse BSONArray()
      val subjects = bsonArray.values.collect {
        case x:BSONDocument => SubjectBSONCodec.read(x)
      }
      val name = bson.getAs[String]("name")
      if(name.isEmpty) log.warn("The universe is about to collapse...")
      new School(bson.getAs[BSONObjectID]("_id"), name.get, subjects.toList)
    }
  }

  implicit object SubjectBSONCodec extends BSONDocumentReader[Subject] with BSONDocumentWriter[Subject] {
    def write(t: Subject) = t.toBSON // BSONDocument("name"->t.name, "results"->t.results)

    def read(bson: BSONDocument) = {
      val results = bson.getAs[BSONArray]("results").get.values.toList.collect{
        case x:BSONDocument => ResultBSONCodec.read(x)
      }
      val name = bson.getAs[String]("name")
      if(name.isEmpty) log.warn("The universe is about to collapse...")
      new Subject(bson.getAs[BSONObjectID]("_id"), name.get, results)
    }
  }
  implicit object ResultBSONCodec extends BSONDocumentReader[Result] with BSONDocumentWriter[Result] {
    def write(t: Result) = t.toBSON

    def read(bson: BSONDocument) = {
      log.debug("Parse BSON Document for Result")
      val tmp = bson.elements.toList
      tmp.foreach(e => log.debug(s"Value: ${e}"))
      val year = bson.getAs[Int]("year")
      val score = bson.getAs[BSONNumberLike]("score")
      if(year.isEmpty || score.isEmpty) log.warn("The universe is about to collapse")
      new Result(bson.getAs[BSONObjectID]("_id"), year.get, score.get.toLong)
    }
  }
}
