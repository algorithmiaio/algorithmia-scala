package com.algorithmia

object AlgorithmiaConf {
  private val defaultApiAddress: String = "https://api.algorithmia.com"

  val apiAddress: String = getConfigValue("ALGORITHMIA_API").getOrElse(defaultApiAddress)

  /**
    * Get a value from java properties or system envvar
    */
  private def getConfigValue(configKey: String): Option[String] = {
    val envVal = System.getenv(configKey)
    val propVal = System.getProperty(configKey)

    if (propVal != null && propVal.trim().length() > 0) {
      Some(propVal.trim)
    } else if (envVal != null && envVal.trim().length() > 0) {
      Some(envVal.trim)
    } else {
      None
    }
  }

}
