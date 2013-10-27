package com.cinglevue.challenge.model

/**
 * Created with IntelliJ IDEA.
 * User: mabdullah
 * Date: 10/27/13
 * Time: 9:11 PM
 * To change this template use File | Settings | File Templates.
 */
case class School (_id: Option[Long], name: String, subjects:List[Subject])
case class Subject(_id:Option[Long], name:String, results:List[Result])
case class Result(_id:Option[Long], year:Int, score:Long)
