package com.vaadin.demo.ui.views;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;


public class LoginView extends VerticalLayout {


    public LoginView(LoginCallback callback) {
        setSizeFull();
        setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Label title = new Label("Patient Portal");
        title.addStyleName(ValoTheme.LABEL_H1);

        TextField usernameField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        Label errorLabel = new Label();

        Button loginButton = new Button("Login");
        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        loginButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        loginButton.addClickListener(click -> {
            String password = passwordField.getValue();
            passwordField.clear();

            if(!callback.login(usernameField.getValue(), password)){
                errorLabel.setValue("Login failed.");
            }
        });



        VerticalLayout loginBox = new VerticalLayout(title, new FormLayout(usernameField, passwordField), loginButton, errorLabel);
        loginBox.setSizeUndefined();
        addComponent(loginBox);
    }


    @FunctionalInterface
    public interface LoginCallback {
        boolean login(String username, String password);
    }
}
