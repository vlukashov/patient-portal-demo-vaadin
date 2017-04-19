package com.vaadin.demo.ui.e2e;


import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.NativeButtonElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.Assert.*;

public class LoginE2ETest extends TestBenchTestCase {

    @Before
    public void setUp() throws Exception {
        setDriver(new ChromeDriver());
        getDriver().get("http://localhost:8080/");
    }


    @Test
    public void incorrectLoginShowsErrorMessage(){
        TextFieldElement loginusername = $(TextFieldElement.class).id("login-username");
        PasswordFieldElement loginpassword = $(PasswordFieldElement.class).id("login-password");
        NativeButtonElement loginbutton = $(NativeButtonElement.class).id("login-button");

        loginusername.sendKeys("foo");
        loginpassword.sendKeys("bar");
        loginbutton.click();

        assertNotNull($(LabelElement.class).id("login-error"));
    }

    @Test
    public void correctLoginShowsMainView(){
        TextFieldElement loginusername = $(TextFieldElement.class).id("login-username");
        PasswordFieldElement loginpassword = $(PasswordFieldElement.class).id("login-password");
        NativeButtonElement loginbutton = $(NativeButtonElement.class).id("login-button");

        loginusername.sendKeys("user");
        loginpassword.sendKeys("password");
        loginbutton.click();

        // Assert that we can see the logout button on the main view
        assertNotNull($(NativeButtonElement.class).id("nav-logout"));
    }


    @After
    public void tearDown() throws Exception {
        getDriver().quit();
    }
}
