import java.io._
import java.util.Scanner
import com.algorithmia.Algorithmia
import com.algorithmia.data._
import org.junit.{Assert, Test}
import org.specs2.mutable._

abstract class DataFileGenericTest extends Specification {
  def getFullPath(thing: String): String

  val key: String = System.getenv("ALGORITHMIA_API_KEY")
  "api key" should {
    "define environment variable ALGORITHMIA_API_KEY" in {
      key must not beNull
    }
  }

  val largeFileName = "/tmp/3GB"
  val largeFile = new File(largeFileName)
  this.synchronized {
    if (!largeFile.exists) {
      val procBuilder = new ProcessBuilder("dd", "if=/dev/zero", "of=" + largeFileName, "bs=1G", "count=3")
      Assert.assertEquals(procBuilder.start.waitFor, 0)
    }
  }
  "test file" should {
    "have right size" in {
      largeFile.length mustEqual 3221225472L
    }
  }

  "path operations" should {

    "get filename" in {
      val file = new DataDirectory(null, getFullPath("javaDataFileName/foo"))
      file.getName mustEqual "foo"
    }

    "get parent" in {
      val file = new DataFile(null, getFullPath("javaclienttest/foo"))
      val parent = new DataDirectory(null, getFullPath("javaclienttest"))
      parent.path mustEqual file.getParent.path
    }

  }

  "put operations" should {

    "create file" in {
      val file = Algorithmia.client(key).file(getFullPath("javaDataFileCreate/foo.txt"))
      // Make sure test starts in clean state
      if (file.exists) file.delete()
      if (!file.getParent.exists) file.getParent.create()
      val sampleFile = new File(this.getClass.getResource("sample.txt").getFile)
      file.put(sampleFile)
      file.exists must beTrue
      file.delete()
      file.exists must beFalse
    }

    "string upload" in {
      val file = Algorithmia.client(key).file(getFullPath("javaDataFileUpload/foo.txt"))
      if (file.exists) file.delete()
      if (!file.getParent.exists) file.getParent.create()
      // Write expected string to a local temp file
      val expected = "This is a cloud: ☁" //Unicode codepoint: U+2601
      file.put(expected)
      file.exists must beTrue
      file.getString mustEqual expected
    }

    "file upload" in {
      val file = Algorithmia.client(key).file(getFullPath("javaDataFileUpload/foo.txt"))
      if (file.exists) file.delete()
      if (!file.getParent.exists) file.getParent.create()
      val expected = "This is a cloud: ☁"
      val temp = File.createTempFile("tempfile", ".tmp")
      val bw = new BufferedWriter(new FileWriter(temp))
      bw.write(expected)
      bw.close()
      file.put(temp)
      file.exists must beTrue
      file.getString mustEqual expected
    }

    "large file upload" in {
      val file = Algorithmia.client(key).file(getFullPath("largeFiles/3GB_file"))
      if (!file.getParent.exists) file.getParent.create()
      if (!file.exists) file.put(largeFile)
      file.exists must beTrue
    }

    "large file upload stream" in {
      val file = Algorithmia.client(key).file(getFullPath("largeFiles/3GB_input_stream"))
      if (!file.getParent.exists) file.getParent.create()
      if (!file.exists) file.put(new FileInputStream(largeFile))
      file.exists must beTrue
    }

  }

  "get operations" should {

    "get string" in {
      val file = Algorithmia.client(key).file(getFullPath("javaDataFileGet/foo.txt"))
      val expected = "Simple text file"
      if (!file.getParent.exists) file.getParent.create()
      if (!file.exists) file.put(expected)
      file.exists must beTrue
      file.getString mustEqual expected
    }

    "get large file" in {
      val COUNT = 1000000
      val file = Algorithmia.client(key).file(getFullPath("largeFiles/" + COUNT + "Numbers"))
      val numbersFile = File.createTempFile("TestGetLargeFile", "Numbers")
      val ps = new PrintStream(numbersFile)
      var i = 0
      while (i < COUNT) {
        ps.println(i)
        i += 1
      }
      if (!file.getParent.exists) file.getParent.create()
      if (!file.exists) file.put(numbersFile)
      val downloaded = file.getFile
      downloaded.length mustEqual numbersFile.length
      val in = new Scanner(downloaded)
      var lines = 0
      while (in.hasNextLine) {
        lines mustEqual in.nextLine.toInt
        lines += 1
      }
      lines mustEqual COUNT
      numbersFile.delete must beTrue
    }

  }

  "file types" should {
    "files are files" in {
      val file = Algorithmia.client("").file(getFullPath("javaDataFileGet/foo.txt"))
      file.isFile must beTrue
      file.isDirectory must beFalse
      file.getType mustEqual DataFileType
    }
  }

}
