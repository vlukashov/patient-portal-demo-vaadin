package com.vaadin.demo.ui.views;


import org.junit.Test;

import static org.junit.Assert.*;

public class LoginViewTest {

    @Test
    public void correctDataShouldBeSentOnLogin() {
        String username = "username";
        String password = "password";
        LoginView view = new LoginView(loginData -> {
            assertTrue(username.equals(loginData.getUsername()));
            assertTrue(password.equals(loginData.getPassword()));
            return true;
        });

        view.usernameField.setValue(username);
        view.passwordField.setValue(password);
        view.loginButton.click();
    }

    @Test
    public void errorMessageShouldBeShownOnFailure() {
        LoginView view = new LoginView(loginData -> false);

        view.usernameField.setValue("foo");
        view.passwordField.setValue("bar");
        view.loginButton.click();

        assertTrue(view.errorLabel.getValue().contains("failed"));
    }
}
