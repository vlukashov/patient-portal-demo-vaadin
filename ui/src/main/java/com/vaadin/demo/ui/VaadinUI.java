package com.vaadin.demo.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.demo.ui.security.SecurityUtils;
import com.vaadin.demo.ui.views.LoginView;
import com.vaadin.demo.ui.views.MainView;
import com.vaadin.server.*;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringUI
@Theme("portal")
@Widgetset("com.vaadin.demo.ui.PatientPortalWidgetSet")
public class VaadinUI extends UI {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    MainView mainView;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        if(SecurityUtils.isLoggedIn()){
            showMainView();
        } else {
            showLoginView();
        }
    }

    private void showLoginView() {
        setContent(new LoginView(this::login));
    }

    private void showMainView() {
       setContent(mainView);
    }

    public boolean login(String username, String password) {
        try {
            Authentication token = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            // Reinitialize the session to protect against session fixation attacks. This does not work
            // with websocket communication.
            VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
            SecurityContextHolder.getContext().setAuthentication(token);
            // Now when the session is reinitialized, we can enable websocket communication. Or we could have just
            // used WEBSOCKET_XHR and skipped this step completely.
            getPushConfiguration().setTransport(Transport.WEBSOCKET);
            getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
            // Show the main UI
            showMainView();
            return true;
        } catch (AuthenticationException ex) {
            return false;
        }
    }
}
