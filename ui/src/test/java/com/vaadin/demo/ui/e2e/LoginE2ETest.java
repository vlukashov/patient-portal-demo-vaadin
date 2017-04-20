package com.vaadin.demo.ui.e2e;


import com.vaadin.testbench.TestBenchTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.Assert.assertNotNull;

public class LoginE2ETest extends TestBenchTestCase {

    private LoginViewPO page;

    @Before
    public void setUp() throws Exception {
        setDriver(new ChromeDriver());
        page = new LoginViewPO(getDriver());
        page.navigateTo();
    }


    @Test
    public void incorrectLoginShowsErrorMessage(){
        page.getUsernameInput().sendKeys("foo");
        page.getPasswordInput().sendKeys("bar");
        page.getLoginButton().click();

        assertNotNull(page.getErrorLabel());
    }

    @Test
    public void correctLoginShowsMainView(){
        page.getUsernameInput().sendKeys("user");
        page.getPasswordInput().sendKeys("password");
        page.getLoginButton().click();

        assertNotNull(new MainViewPO(getDriver()).isLoaded());
    }


    @After
    public void tearDown() throws Exception {
        getDriver().quit();
    }
}
