package com.algorithmia.data

import play.api.libs.json._

case class DataAcl(read: DataAclType)

sealed trait DataAclType {
  def toJson: JsValue
}

case object DataPublic extends DataAclType {
  def toJson: JsValue = Json.arr("user://*")
}
case object DataMyAlgorithms extends DataAclType {
  def toJson: JsValue = Json.arr("algo://.my/*")
}
case object DataPrivate extends DataAclType {
  def toJson: JsValue = Json.arr()
}

object DataAclType {
  implicit object DataAclTypeReads extends Reads[DataAclType] {
    def reads(json: JsValue): JsResult[DataAclType] = json match {
      case JsArray(Seq(JsString("user://*"))) => JsSuccess(DataPublic)
      case JsArray(Seq(JsString("algo://.my/*"))) => JsSuccess(DataMyAlgorithms)
      case JsArray(Seq()) => JsSuccess(DataPrivate)
      case _ => JsError()
    }
  }
  implicit object DataAclTypeWrites extends Writes[DataAclType] {
    def writes(acl: DataAclType): JsValue = acl.toJson
  }
}
object DataAcl {
  implicit val dataAclReads: Reads[DataAcl] = Json.reads[DataAcl]
  implicit val dataAclWrites: Writes[DataAcl] = Json.writes[DataAcl]
}
