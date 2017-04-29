import com.algorithmia.Algorithmia
import com.algorithmia.data._
import java.util
import org.specs2.mutable.Specification

abstract class DataDirectoryGenericTest extends Specification {
  def getFullPath(thing: String): String

  val key: String = System.getenv("ALGORITHMIA_API_KEY")
  "api key" should {
    "define environment variable ALGORITHMIA_API_KEY" in {
      key must not beNull
    }
  }

  private def fuzzyDirectoryMatch(one: String, two: String): Boolean = {
    if (one.length == two.length) one == two
    else if (one.length < two.length) one + "/" == two
    else one == two + "/"
  }

  "path operations" should {

    "get dirname" in {
      val dir = new DataDirectory(null, getFullPath("javaclienttest"))
      dir.getName mustEqual "javaclienttest"
    }

    "get parent" in {
      val dir = new DataDirectory(null, getFullPath("javaclienttest"))
      val parent = new DataDirectory(null, getFullPath(""))
      parent.path mustEqual dir.getParent.path
      fuzzyDirectoryMatch(parent.path, dir.getParent.path) must beTrue
    }

  }

  "create operations" should {
    "create directory" in {
      val dir = Algorithmia.client(key).dir(getFullPath("javaDataDirCreate"))
      // Make sure test starts in clean state
      if (dir.exists) dir.delete(true)
      dir.create()
      dir.exists must beTrue
      dir.delete(false)
      dir.exists must beFalse
    }
  }

  "list operations" should {
    "list directory" in {
      val parentDir = "javaDataDirList"
      val dir = Algorithmia.client(key).dir(getFullPath(parentDir))
      if (dir.exists) dir.delete(true)
      dir.create()
      dir.file("foo").put("bar")
      dir.file("foo2").put("bar2")
      val filesFound = new util.HashSet[String]
      var numFiles = 0
      for (file <- dir.files) {
        numFiles += 1
        filesFound.add(file.toString) must beTrue
      }
      numFiles mustEqual 2
      filesFound.contains(getJoinedName(parentDir, "foo")) must beTrue
      filesFound.contains(getJoinedName(parentDir, "foo2")) must beTrue
    }

    "list with trailing slash" in {
      dataDirListIterable("javaDataDirList1/")
    }

    "list without trailing slash" in {
      dataDirListIterable("javaDataDirList2")
    }

    "list large directory" in {
      val dir = Algorithmia.client(key).dir(getFullPath("javaLargeDataDirList1"))
      val NUM_FILES = 1100
      val EXTENSION = ".txt"
      // Since this test uploads a lot of files to the server, we want to recreate
      // this directory only when it does not already exist.
      if (!dir.exists) {
        dir.create()
        var i = 0
        while (i < NUM_FILES) {
          dir.file(i + EXTENSION).put(i + "")
          i += 1
        }
      }
      val seenFiles = new Array[Boolean](NUM_FILES)
      var numFiles = 0
      for (file <- dir.files) {
        numFiles += 1
        val fileName = file.getName
        val endIndex = fileName.length - EXTENSION.length
        val index = fileName.substring(0, endIndex).toInt
        seenFiles(index) = true
      }
      var allSeen = true
      for (cur <- seenFiles) {
        allSeen = allSeen && cur
      }
      numFiles mustEqual NUM_FILES
      allSeen must beTrue
    }

    "list empty directory" in {
      val dir = Algorithmia.client(key).dir(getFullPath("test_empty_dir"))
      if (dir.exists) dir.delete(true)
      dir.create()
      var dirCount = 0
      for (childDir <- dir.dirs) {
        dirCount += 1
      }
      dirCount mustEqual 0
      var fileCount = 0
      for (childFile <- dir.files) {
        fileCount += 1
      }
      dir.delete(true)
      fileCount mustEqual 0
    }

  }

  private def getJoinedName(one: String, two: String): String = {
    val result: String = if (one.endsWith("/"))
      getFullPath(one) + two
    else
      getFullPath(one) + "/" + two
    if (!result.startsWith("data://"))
      "data://" + result
    else
      result
  }

  @throws[Exception]
  private def dataDirListIterable(dirName: String) = {
    val dir = Algorithmia.client(key).dir(getFullPath(dirName))
    if (dir.exists) dir.delete(true)
    dir.create()
    dir.file("foo").put("bar")
    dir.file("foo2").put("bar2")
    val filesFound = new util.HashSet[String]
    var numFiles = 0
    for (file <- dir.files) {
      numFiles += 1
      filesFound.add(file.toString) must beTrue
    }
    numFiles mustEqual 2
    filesFound.contains(getJoinedName(dirName, "foo")) must beTrue
    filesFound.contains(getJoinedName(dirName, "foo2")) must beTrue
  }

  "file types" should {
    "directories are directories" in {
      val dir = Algorithmia.client(key).dir(getFullPath("javaDataFileGet"))
      dir.isFile must beFalse
      dir.isDirectory must beTrue
      dir.getType mustEqual DataDirectoryType
    }
  }

}
