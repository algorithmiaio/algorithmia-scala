package algorithmia

import algorithmia.algorithms._
import scalaj.http._

case class Algorithmia(apiKey: String) {

    def algorithm(username: String, algoname: String, version: Version): Algorithm = {
        Algorithm(this.getClient, username, algoname, version)
    }

    def algorithm(algoUri: String): Algorithm = Algorithm(this.getClient, algoUri)

    // def collection(dataUri: String): Collection = Collection(this.getClient, dataUri)

    private def getClient(): Client = { Client(this.apiKey) }
}


object Algorithmia {
    val apiBaseUrl: String = "https://api.algorithmia.com"
}

case class Client(apiKey: String) {
    val userAgent = s"scala algorithmia.scala"
    val baseUrl = s"https://api.algorithmia.com"

    def post(url: String, data: String): HttpResponse[String] = {
        // println(s"DEBUG URL: $url")
        // println(s"DEBUG DATA: $data")
        Http(url)
            .postData(data)
            .header("Content-Type", "application/json")
            .header("User-Agent", userAgent)
            .header("Authorization", this.apiKey)
            .header("Accept","application/json")
            .option(HttpOptions.connTimeout(60000))
            .asString
    }
}

