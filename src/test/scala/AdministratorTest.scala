import com.algorithmia.Algorithmia
import com.algorithmia.algo.Administrator
import com.algorithmia.handler.{Organization, User}
import org.specs2.mutable._

object AdministratorTest extends Specification {
  val keys: String = System.getenv("ALGORITHMIA_ADMIN_API_KEY")
  val client = Algorithmia.client(keys)
  val admin = new Administrator(client)
  val randomId = System.currentTimeMillis();
  val orgName = s"jakeOrg_${randomId}"
  val userName = s"jake_${randomId}"

  "api key" should {
    "define environment variable Administrator" in {
      keys must not be null
    }
  }

  "Administrator can create organization" should {
    "jakeOrg_${randomId}" in {
      val org = Organization(orgName, s"jakeOrg_Label_${randomId}", "Jake_org", "jake_org@algotest.com", "organization", "")
      admin.createOrganization(org) must be
    }
  }

  "Administrator can create users" should {
    s"jake_${randomId}" in {
      val user = User(userName, s"jake_${randomId}@algo.test", "Jake_org",  "user", "")
      admin.createUser(user) must be
    }
  }

  "Administrator can add members to organization" should {
    "" in {
      admin.addOrganizationMember(orgName, userName) must be
    }
  }

}
