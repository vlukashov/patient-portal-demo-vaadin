package com.vaadin.demo.ui.e2e;

import com.vaadin.testbench.TestBenchTestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class MainViewPO extends TestBenchTestCase {

    public MainViewPO(WebDriver driver) {
        setDriver(driver);
    }


    WebElement getLogoutButton() {
        return findElement(By.id("nav-logout"));
    }

    boolean isLoaded() {
        return getLogoutButton() != null;
    }
}
