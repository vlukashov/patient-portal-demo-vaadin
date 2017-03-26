package com.vaadin.demo.ui.views;

import com.vaadin.demo.ui.views.base.NavBar;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeButton;

class MainNavBar extends NavBar {

    MainNavBar() {
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
            button.addClickListener(click -> getUI().getNavigator().navigateTo(name));
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
    }
}
