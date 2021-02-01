package com.algorithmia.algo
import java.io.IOException

import com.algorithmia.handler.{Organization, User, OrganizationType}
import com.algorithmia.{AlgorithmiaClient, AlgorithmiaConf}
import play.api.libs.json.{JsError, JsSuccess, Json}

class Administrator(client: AlgorithmiaClient){
  val url: String = if(AlgorithmiaConf.apiAddress.equalsIgnoreCase("https://api.algorithmia.com"))
    "http://localhost:9000" else AlgorithmiaConf.apiAddress

  def createUser(user:User):String ={
    val input = Json.toJson(user)
    val response = client.http.post(url + "/v1/users", input.toString)
    if (response.code == 200) {
      val responseJson = Json.parse(response.body)
      Json.fromJson[User](responseJson) match {
        case JsSuccess(user, _) => user.userName
        case error: JsError => throw new IOException(s"Failed to create user $error $responseJson")
      }
    } else {
      throw new IOException("Failed to create user , status code " + response.code)
    }
  }

  def createOrganization(org: Organization): String = {
    val input = Json.toJson(org)
    val response = this.client.http.post(url + "/v1/organizations", input.toString)
    if (response.code == 200) {
      val responseJson = Json.parse(response.body)
      Json.fromJson[Organization](responseJson) match {
        case JsSuccess(organization, _) => organization.orgName
        case error: JsError => throw new IOException(s"Failed to create organization $error $responseJson")
      }
    } else {
      throw new IOException("Failed to create organization , status code " + response.code)
    }
  }

  def updateOrganization(orgName: String, org: Organization): Boolean = {
    val input = Json.toJson(org)
    val response = this.client.http.put(url + s"/v1/organizations/${orgName}", input.toString.getBytes)
    if (response.code == 204) {
      true
    } else {
      throw new IOException("Failed to update organization , status code " + response.code)
    }
  }

  def getOrganization(orgName: String): Organization = {
    val response = this.client.http.get(url + s"/v1/organizations/${orgName}")
    if (response.code == 200) {
      val responseJson = Json.parse(response.body)
      Json.fromJson[Organization](responseJson) match {
        case JsSuccess(organization, _) => organization
        case error: JsError => throw new IOException(s"Failed to get organization $error $responseJson")
      }
    } else {
      throw new IOException("Failed to get organization , status code " + response.code)
    }
  }

  def getOrganizationTypeId(orgType: String): String = {
    val response = this.client.http.get(url + s"/v1/organization/types")
    if (response.code == 200) {
      val orgTypes: List[OrganizationType] = Json.parse(response.body).as[List[OrganizationType]]
      orgTypes.filter(o => o.name == orgType).head.id.toString
    } else {
      throw new IOException("Failed to get organization types, status code " + response.code)
    }
  }

  def deleteOrganization(orgName: String): Boolean = {
    val response = this.client.http.delete(url + s"/v1/organizations/${orgName}")
    if (response.code == 204) {
      true
    } else {
      throw new IOException("Failed to delete organization , status code " + response.code)
    }
  }

  def addOrganizationMember(orgName: String, userName: String): String = {
    val path = url + "/v1/organizations/" + orgName + "/members/" + userName
    val response = this.client.http.put(path, null)
    if (response.code == 200) {
      response.body
    } else {
      throw new IOException("Failed to add member to organization , status code " + response.code)
    }
  }

}
