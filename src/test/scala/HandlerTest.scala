import org.specs2.mutable.Specification
import org.specs2.specification.{AfterEach, BeforeEach}
import algorithms._

class HandlerTest extends Specification with BeforeEach with AfterEach {

  val PIPE: String = "/tmp/algoout"

  override protected def before: Any = {
    val p = Runtime.getRuntime.exec(s"touch $PIPE")
    p.waitFor
  }

  override protected def after: Any = {
    val p = Runtime.getRuntime.exec(s"rm $PIPE")
    p.waitFor()
  }

  // TODO: Build out tests


}
