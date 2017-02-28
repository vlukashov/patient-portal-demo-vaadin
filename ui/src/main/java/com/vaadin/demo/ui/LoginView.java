package com.vaadin.demo.ui;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.dd.acceptcriteria.Not;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;


public class LoginView extends VerticalLayout {


    private TextField username;
    private PasswordField password;
    private LoginSuccessListener listener;
    private Label errorLabel;

    public LoginView(LoginSuccessListener listener) {
        this.listener = listener;
        setSizeFull();
        setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        createLoginBox();
    }

    private void createLoginBox() {
        Label title = new Label("Patient Portal");
        title.addStyleName(ValoTheme.LABEL_H1);

        username = new TextField("Username");
        password = new PasswordField("Password");

        Button loginButton = new Button("Login", click -> login());
        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        loginButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

        errorLabel = new Label();

        VerticalLayout loginBox = new VerticalLayout(title, new FormLayout(username, password), loginButton, errorLabel);
        loginBox.setSizeUndefined();
        addComponent(loginBox);
    }

    private void login(){
        if("user".equals(username.getValue()) && "password".equals(password.getValue())){
            listener.loginSuccessful();
        } else {
            errorLabel.setValue("Login failed.");
        }
    }


    public interface LoginSuccessListener {
        void loginSuccessful();
    }
}
