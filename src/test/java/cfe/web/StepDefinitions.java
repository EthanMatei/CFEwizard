package cfe.web;
import org.htmlunit.WebAssert;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import junit.framework.Assert;
import io.cucumber.java.en.Then;

public class StepDefinitions {
    public static String CFE_WIZARD_URL = "http://localhost:8080/CFE/SystemStatusAction.action";

    private WebClient webClient;
    private HtmlPage page;

    @Before
    public void init() throws Exception {
        webClient = new WebClient();
    }

    @After
    public void close() throws Exception {
        webClient.close();
    }
    
    @Given("I am logged into the CFE Wizard")
    public void iAmLoggedIntoTheCfeWizard() throws Exception {
        this.page = webClient.getPage(CFE_WIZARD_URL);
    }
    
    //@Then("I should see {String}")
    //public void iShouldSee(String text) throws Exception {
    //    WebAssert.assertTextPresent(this.page, "");
    //}
    

    @Given("I have {int} cukes in my belly")
    public void i_have_n_cukes_in_my_belly(int cukes) {
        System.out.format("Cukes: %n\n", cukes);
    }
}

