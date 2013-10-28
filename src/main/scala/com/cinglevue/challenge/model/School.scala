package com.cinglevue.challenge.model

import reactivemongo.bson._

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
  implicit object SchoolBSONCodec extends BSONDocumentReader[School] with BSONDocumentWriter[School] {
    def write(t: School):BSONDocument = t.toBSON

    def read(bson: BSONDocument):School = {
      val subjects = bson.getAs[BSONArray]("subjects").get.values.collect {
        case x:BSONDocument => SubjectBSONCodec.read(x)
      }
      new School(bson.getAs[BSONObjectID]("_id"), bson.getAs[String]("name").get, subjects.toList)
    }
  }

  implicit object SubjectBSONCodec extends BSONDocumentReader[Subject] with BSONDocumentWriter[Subject] {
    def write(t: Subject) = t.toBSON // BSONDocument("name"->t.name, "results"->t.results)

    def read(bson: BSONDocument) = {
      val results = bson.getAs[BSONArray]("results").get.values.toList.collect{
        case x:BSONDocument => ResultBSONCodec.read(x)
      }
      new Subject(bson.getAs[BSONObjectID]("_id"), bson.getAs[String]("name").get, results)
    }
  }
  implicit object ResultBSONCodec extends BSONDocumentReader[Result] with BSONDocumentWriter[Result] {
    def write(t: Result) = t.toBSON

    def read(bson: BSONDocument) = {
      new Result(bson.getAs[BSONObjectID]("_id"), bson.getAs[Int]("year").get, bson.getAs[Long]("score").get)
    }
  }
}