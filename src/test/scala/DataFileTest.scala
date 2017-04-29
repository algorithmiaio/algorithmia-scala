
class DataFileTest extends DataFileGenericTest {
  override def getFullPath(path: String): String = "data://.my/" + path
}
