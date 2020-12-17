package com.algorithmia.handler
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._


case class Organization(orgName: String,  orgLabel: String, orgContactName: String, orgEmail: String,
                         resourceType: String, selfLink: String)

object Organization {
  implicit val organizationReads: Reads[Organization] = (
    (JsPath \ "org_name").read[String] and
    (JsPath \ "org_label").read[String] and
    (JsPath \ "org_contact_name").read[String] and
    (JsPath \ "org_email").read[String] and
    (JsPath \ "resource_type").read[String] and
    (JsPath \ "self_link").read[String]
    )(Organization.apply _)

  implicit val organizationWrites: Writes[Organization] = (
    (JsPath \ "org_name").write[String] and
    (JsPath \ "org_label").write[String] and
    (JsPath \ "org_contact_name").write[String] and
    (JsPath \ "org_email").write[String] and
    (JsPath \ "resource_type").write[String] and
    (JsPath \ "self_link").write[String]
    )(unlift(Organization.unapply))

}

case class User(userName:String, email: String, fullName: String, resourceType: String, selfLink: String)

object User {
  implicit val userReads: Reads[User] = (
    (JsPath \ "username").read[String] and
    (JsPath \ "email").read[String] and
    (JsPath \ "fullname").read[String] and
    (JsPath \ "resource_type").read[String] and
    (JsPath \ "self_link").read[String]
    )(User.apply _)

  implicit val organizationWrites: Writes[User] = (
    (JsPath \ "username").write[String] and
    (JsPath \ "email").write[String] and
    (JsPath \ "fullname").write[String] and
    (JsPath \ "resource_type").write[String] and
    (JsPath \ "self_link").write[String]
    )(unlift(User.unapply))

}
