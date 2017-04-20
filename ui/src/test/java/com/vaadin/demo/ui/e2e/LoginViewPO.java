package com.vaadin.demo.ui.e2e;


import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchTestCase;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginViewPO extends TestBenchTestCase{

    public LoginViewPO(WebDriver driver) {
        setDriver(driver);
    }

    void navigateTo(){
        getDriver().get("http://localhost:8080/");
    }

    WebElement getUsernameInput() {
        return findElement(By.id("login-username"));
    }

    WebElement getPasswordInput(){
        return findElement(By.id("login-password"));
    }

    WebElement getLoginButton(){
        return findElement(By.id("login-button"));
    }

    WebElement getErrorLabel() {
        return findElement(By.id("login-error"));
    }
}
