package cfe.web;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.htmlunit.WebAssert;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
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
        
        System.out.println("URL: " + this.cfeUrl);
        HtmlPage htmlPage = this.page.getPage();
        System.out.println(this.page.getVisibleText());
        
        HtmlTextInput usernameInput = htmlPage.getHtmlElementById("username");
        usernameInput.setValueAttribute(this.cfeUsername);
        System.out.println("ATTRIBUTE VALUE: " + usernameInput.getValueAttribute());
        
        HtmlTextInput passwordInput = htmlPage.getHtmlElementById("username");
        passwordInput.setValueAttribute(this.cfePassword);
        
        System.out.println(this.cfeUsername + ": " + this.cfePassword);
        
        HtmlForm loginForm = htmlPage.getFormByName("loginForm");
        SubmittableElement s = loginForm.getOneHtmlElementByAttribute("input", "type", "submit");
        loginForm.submit(s);
        //HtmlPage newPage = submit.click();
        
        //loginForm.get
        //HtmlElement submit = this.page.getHtmlElementById("loginSubmit");
        //loginForm.submit();
        //this.page = submit.click();
        
        System.out.println(this.page.getVisibleText());
    }
    
    @Then("I should see {string}")
    public void iShouldSee(String text) throws Exception {
        WebAssert.assertTextPresent(this.page, text);
    }
    

    @Given("I have {int} cukes in my belly")
    public void i_have_n_cukes_in_my_belly(int cukes) {
        System.out.format("Cukes: %n\n", cukes);
    }
}

