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
      case JsArray(arr) => arr.headOption match {
        case Some(JsString("user://*")) => JsSuccess(DataPublic)
        case Some(JsString("algo://.my/*")) => JsSuccess(DataMyAlgorithms)
        case None => JsSuccess(DataPrivate)
        case _ => JsError(s"unexpected data acl: $json")
      }
      case _ => JsError(s"unexpected data acl: $json")
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
