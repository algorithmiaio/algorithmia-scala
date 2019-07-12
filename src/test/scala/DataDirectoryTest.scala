import com.algorithmia.Algorithmia
import com.algorithmia.data._

object DataDirectoryTest {
  private val pathPrefix = ".my/"
}

class DataDirectoryTest extends DataDirectoryGenericTest {
  override def getFullPath(thing: String): String = DataDirectoryTest.pathPrefix + thing

  "directory permissions" should {

    "get directory permissions" in {
      val dir = Algorithmia.client(key).dir("data://.my/javaGetPermissions")
      if (dir.exists) dir.delete(true)
      dir.create()
      dir.getPermissions.read mustEqual DataMyAlgorithms
    }

    "create directory with permissions" in {
      val dir = Algorithmia.client(key).dir("data://.my/javaCreateWithPermissions")
      if (dir.exists) dir.delete(true)
      dir.create(Some(DataAcl(read = DataPublic)))
      dir.getPermissions.read mustEqual DataPublic
    }

    "update directory permissions" in {
      val dir = Algorithmia.client(key).dir("data://.my/javaUpdatePermissions")
      if (dir.exists) dir.delete(true)
      dir.create(Some(DataAcl(read = DataPublic)))
      dir.getPermissions.read mustEqual DataPublic

      dir.updatePermissions(DataAcl(read = DataPrivate)) must beTrue
      dir.getPermissions.read mustEqual DataPrivate
    }

  }

}
