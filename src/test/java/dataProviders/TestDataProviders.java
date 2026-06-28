package dataProviders;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.DataProvider;
import utils.JsonUtils;

public class TestDataProviders {

    @DataProvider(name = "signUpData")
    public Object[][] getSignUpData() {
        JsonNode root = JsonUtils.loadJson("testdata/signup_data.json");
        return JsonUtils.toDataProviderArray(root,
                "testCaseID", "userName", "email", "password", "role", "expectedResult", "expectedMessage");
    }

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        JsonNode root = JsonUtils.loadJson("testdata/login_data.json");
        return JsonUtils.toDataProviderArray(root,
                "testCaseID", "email", "password", "expectedResult", "expectedMessage");
    }

    @DataProvider(name = "brandLoginData")
    public Object[][] getBrandLoginData() {
        JsonNode root = JsonUtils.loadJson("testdata/campaign_data.json");
        JsonNode login = root.get("brandLogin");
        return new Object[][] {{
            login.get("testCaseID").asText(),
            login.get("email").asText(),
            login.get("password").asText(),
            login.get("expectedResult").asText(),
            login.get("expectedMessage").asText()
        }};
    }

    @DataProvider(name = "createCampaignData")
    public Object[][] getCreateCampaignData() {
        JsonNode root = JsonUtils.loadJson("testdata/campaign_data.json");
        return JsonUtils.toDataProviderArray(root.get("createCampaigns"),
                "testCaseID", "name", "description", "startDate", "endDate", "platform", "budget", "eligibility");
    }

    @DataProvider(name = "negativeCampaignData")
    public Object[][] getNegativeCampaignData() {
        JsonNode root = JsonUtils.loadJson("testdata/campaign_data.json");
        return JsonUtils.toDataProviderArray(root.get("negativeCampaigns"),
                "testCaseID", "name", "description", "startDate", "endDate", "platform", "budget", "eligibility");
    }
}