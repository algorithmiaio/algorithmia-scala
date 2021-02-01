import com.algorithmia.Algorithmia
import com.algorithmia.algo.Administrator
import com.algorithmia.handler.{Organization, User}
import org.specs2.mutable._

class AdministratorTest extends Specification {
  sequential
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
    s"jakeOrg_${randomId}" in {
      val org = Organization(
        orgName,
        s"jakeOrg_Label_${randomId}",
        "Jake_org",
        s"${randomId}@algotest.com",
        s"${randomId}",
        "external_admin_group_id",
        "external_admin_group_id",
        "organization",
        "",
        "basic"
      )
      val newOrgName = admin.createOrganization(org)
      org.orgName mustEqual (newOrgName)
    }
  }

  "Administrator can update organization" should {
    s"jakeOrg_${randomId}" in {
      val updatedOrg = Organization(
        s"updated_${orgName}",
        s"jakeOrg_NewLabel_${randomId}",
        "Jake_org_Updated",
        s"${randomId}@algotest.com",
        s"${randomId}",
        "external_admin_group_id",
        "external_admin_group_id",
        "organization",
        "",
        "basic"
      )
      admin.updateOrganization(orgName, updatedOrg) mustEqual true
    }
  }

  "Administrator can create users" should {
    s"jake_${randomId}" in {
      val user = User(userName, s"jake_${randomId}@algo.test", "Jake_org", "user", "")
      admin.createUser(user) must be
    }
  }

  "Administrator can add members to organization" should {
    "" in {
      admin.addOrganizationMember(orgName, userName) must be
    }
  }

  "Administrator can get organization" should {
    "" in {
      admin.getOrganization(orgName) must be
    }
  }

  "Administrator can delete organization" should {
    "" in {
      admin.deleteOrganization(orgName) mustEqual true
    }
  }

}
