publishTo := (version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}).value

// Stuff sonatype wants
pomExtra := (
  <url>http://www.github.com/algorithmiaio/algorithmia-scala</url>
  <licenses>
    <license>
      <name>The MIT License (MIT)</name>
      <url>http://opensource.org/licenses/mit-license.php</url>
       <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <connection>scm:git:git@github.com:algorithmiaio/algorithmia-scala.git</connection>
    <url>https://github.com/algorithmiaio/algorithmia-scala</url>
  </scm>
  <developers>
    <developer>
      <name>Algorithmia</name>
      <email>support@algorithmia.com</email>
      <organization>Algorithmia</organization>
    </developer>
  </developers>
)

// Stuff sonatype does not want
pomIncludeRepository := { _ => false }
