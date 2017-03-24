package com.vaadin.demo.ui.views;

import com.vaadin.demo.ui.util.Nav;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainNavigation extends HorizontalLayout {

    private Map<String, Button> navButtons = new LinkedHashMap<>();

    public MainNavigation() {
        addStyleName("main-nav");
        setSpacing(false);
        setMargin(false);
        addAttachListener(attachEvent -> setupView());
    }

    private void setupView() {
        navButtons.put("patients", new NativeButton("Patients"));
        navButtons.put("analytics", new NativeButton("Analytics"));

        navButtons.forEach((name, button) -> {
            button.addStyleName("link");
            button.addClickListener(click -> Nav.navigateTo(name));
            addComponent(button);
        });

        Button logout = new Button("Logout", click -> {
            Page.getCurrent().reload();
            getSession().close();
        });
        logout.setIcon(VaadinIcons.SIGN_OUT);
        logout.addStyleName("link");

        addComponentsAndExpand(logout);
        logout.setWidth(null);
        setComponentAlignment(logout, Alignment.MIDDLE_RIGHT);

        Nav.addListener(viewChange -> {
            String viewName = viewChange.getViewName();
            navButtons.forEach((name, button) -> {
                if (name.equals(viewName)) {
                    button.addStyleName("active");
                } else {
                    button.removeStyleName("active");
                }
            });

            return true;
        });
    }
}
