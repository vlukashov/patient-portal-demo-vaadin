package com.vaadin.demo.ui.views;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;

public class LoginView extends VerticalLayout {

    public class LoginData {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public LoginView(LoginCallback callback) {
        LoginData loginData = new LoginData();

        addStyleName("login-view");
        setMargin(false);
        setSizeFull();
        setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Label title = new Label("Patient Portal");
        title.addStyleName("h1");

        TextField usernameField = new TextField("Username");
        usernameField.setValueChangeMode(ValueChangeMode.BLUR);
        usernameField.setId("login-username");
        usernameField.setWidth("100%");
        PasswordField passwordField = new PasswordField("Password");
        passwordField.setValueChangeMode(ValueChangeMode.BLUR);
        passwordField.setId("login-password");
        passwordField.setWidth("100%");

        Label errorLabel = new Label();

        NativeButton loginButton = new NativeButton("Login");
        loginButton.setId("login-button");
        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        loginButton.addStyleName("primary");

        VerticalLayout loginBox = new VerticalLayout(
                title,
                usernameField,
                passwordField,
                loginButton,
                errorLabel);
        loginBox.addStyleName("login-box");
        loginBox.setSizeUndefined();
        addComponent(loginBox);

        Binder<LoginData> binder = new Binder<>(LoginData.class);
        binder.forField(usernameField).asRequired("Username required").bind("username");
        binder.forField(passwordField).asRequired("Password required").bind("password");
        binder.readBean(loginData);

        loginButton.addClickListener(click -> {
            try {
                binder.writeBean(loginData);
                if(!callback.login(loginData)){
                    errorLabel.setValue("Login failed.");
                }
            } catch (ValidationException e) {
                errorLabel.setValue("Please fill both fields.");
            }
        });

        usernameField.setValue("user");
        passwordField.setValue("password");
        usernameField.focus();
    }


    @FunctionalInterface
    public interface LoginCallback {
        boolean login(LoginData loginData);
    }
}
