package com.algorithmia.handler
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class Organization(
    orgName: String,
    orgLabel: String,
    orgContactName: String,
    orgEmail: String,
    external_id: String = "",
    external_admin_group: String = "",
    external_member_group: String = "",
    resourceType: String,
    selfLink: String,
    typeId: String
)

object Organization {
  implicit val organizationReads: Reads[Organization] = (
    (JsPath \ "org_name").read[String] and
      (JsPath \ "org_label").read[String] and
      (JsPath \ "org_contact_name").read[String] and
      (JsPath \ "org_email").read[String] and
      (JsPath \ "external_id").read[String] and
      (JsPath \ "external_admin_group").read[String] and
      (JsPath \ "external_member_group").read[String] and
      (JsPath \ "resource_type").read[String] and
      (JsPath \ "self_link").read[String] and
      (JsPath \ "type_id").read[String]
  )(Organization.apply _)

  implicit val organizationWrites: Writes[Organization] = (
    (JsPath \ "org_name").write[String] and
      (JsPath \ "org_label").write[String] and
      (JsPath \ "org_contact_name").write[String] and
      (JsPath \ "org_email").write[String] and
      (JsPath \ "external_id").write[String] and
      (JsPath \ "external_admin_group").write[String] and
      (JsPath \ "external_member_group").write[String] and
      (JsPath \ "resource_type").write[String] and
      (JsPath \ "self_link").write[String] and
      (JsPath \ "type_id").write[String]
  )(unlift(Organization.unapply))

}

case class OrganizationType(id: String, name: String)

object OrganizationType {
  implicit val organizationTypeReads: Reads[OrganizationType] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "name").read[String]
  )(OrganizationType.apply _)

  implicit val organizationTypeWrites: Writes[OrganizationType] = (
    (JsPath \ "id").write[String] and
      (JsPath \ "name").write[String]
  )(unlift(OrganizationType.unapply))
}

case class User(userName: String, email: String, fullName: String, resourceType: String, selfLink: String)

object User {
  implicit val userReads: Reads[User] = (
    (JsPath \ "username").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ "fullname").read[String] and
      (JsPath \ "resource_type").read[String] and
      (JsPath \ "self_link").read[String]
  )(User.apply _)

  implicit val userWrites: Writes[User] = (
    (JsPath \ "username").write[String] and
      (JsPath \ "email").write[String] and
      (JsPath \ "fullname").write[String] and
      (JsPath \ "resource_type").write[String] and
      (JsPath \ "self_link").write[String]
  )(unlift(User.unapply))

}
