package com.algorithmia.algo
import com.algorithmia.{ AlgorithmiaClient, AlgorithmiaConf}
import play.api.libs.json._
import com.google.gson._
import com.google.gson.reflect._

import scala.jdk.CollectionConverters._

class AlgorithmiaInsights(client: AlgorithmiaClient, input:String){
  val url: String = if(AlgorithmiaConf.apiAddress.equalsIgnoreCase("https://api.algorithmia.com"))
    "http://localhost:9000" else AlgorithmiaConf.apiAddress

  def report_insight():AlgoResponse ={
    val gson = new Gson()
    val mapType = new TypeToken[java.util.HashMap[String,String]]{}.getType
    val map = gson.fromJson(this.input, mapType).asInstanceOf[java.util.HashMap[String,String]]
    var insight_payload :JsArray = Json.arr()
    for(p <- map.asScala) {insight_payload = insight_payload:+(Json.obj("insight_key" -> p._1, "insight_value" -> p._2))}
    println("Input Payload : "+insight_payload.toString())
    val httpResponse =(this.client.http.post(url+"/v1/insights", insight_payload.toString))
    val rawOutput = httpResponse.body
    httpResponse.code match {
      case 200 => AlgoResponse(rawOutput, OutputRaw)
      case _ => AlgoFailure(rawOutput, Metadata(0, ContentTypeVoid, None), rawOutput)
    }
    }

}
