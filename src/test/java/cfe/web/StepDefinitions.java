package cfe.web;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.htmlunit.WebAssert;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlPasswordInput;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlTextInput;
import org.htmlunit.html.SubmittableElement;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import junit.framework.Assert;
import io.cucumber.java.en.Then;

public class StepDefinitions {
    public static String WEB_TEST_PROPERTIES_FILE = "webtest.properties";

    private WebClient webClient;
    private HtmlPage page;
    private String cfeUrl;
    private String cfeUsername;
    private String cfePassword;
    

    @Before
    public void init() throws Exception {
        Properties properties = new Properties();
        FileReader reader = new FileReader(WEB_TEST_PROPERTIES_FILE);
        properties.load(reader);
        this.cfeUrl = (String) properties.get("cfe.url");
        this.cfeUsername = (String) properties.getProperty("cfe.username");
        this.cfePassword = (String) properties.getProperty("cfe.password");
        
        webClient = new WebClient();
    }

    @After
    public void close() throws Exception {
        webClient.close();
    }
    
    @Given("I am logged into the CFE Wizard")
    public void iAmLoggedIntoTheCfeWizard() throws Exception {
        this.page = webClient.getPage(this.cfeUrl);
        
        HtmlTextInput usernameInput = this.page.getHtmlElementById("username");
        usernameInput.setText(this.cfeUsername);
        
        HtmlPasswordInput passwordInput = this.page.getHtmlElementById("password");
        passwordInput.setText(this.cfePassword);
        
        HtmlForm loginForm = this.page.getFormByName("loginForm");
        HtmlSubmitInput submitInput = loginForm.getOneHtmlElementByAttribute("input", "type", "submit");
        this.page = submitInput.click();
        
        // System.out.println(this.page.getVisibleText());
    }
    
    @When("I click on link {string}")
    public void iClickOnLink(String linkText) throws Exception {
        HtmlAnchor a = this.page.getAnchorByText(linkText);
        this.page = a.click();
    }
    
    @Then("I should see {string}")
    public void iShouldSee(String text) throws Exception {
        WebAssert.assertTextPresent(this.page, text);
    }
    
}