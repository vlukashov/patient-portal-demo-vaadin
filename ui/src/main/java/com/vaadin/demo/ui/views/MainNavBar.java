package com.vaadin.demo.ui.views;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;

import java.util.LinkedHashMap;
import java.util.Map;

class MainNavBar extends HorizontalLayout {
    private Registration registration;
    private Map<String, Button> navButtons = new LinkedHashMap<>();

    MainNavBar() {
        addStyleName("main-nav");
        setSpacing(false);
        setMargin(false);
    }

    @Override
    public void attach() {
        super.attach();
        setupView();
        registration = getUI().getNavigator().addViewChangeListener(viewChange -> {
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

    private void putNavButton(String viewName, String caption) {
        NativeButton button = new NativeButton(caption);
        button.setId("nav-button-" + viewName);
        navButtons.put(viewName, button);
    }

    private void setupView() {
        putNavButton("patients", "Patients");
        putNavButton("analytics", "Analytics");

        navButtons.forEach((name, button) -> {
            button.addStyleName("link");
            button.addClickListener(click -> getUI().getNavigator().navigateTo(name));
            addComponent(button);
        });

        NativeButton logout = new NativeButton("Logout", click -> {
            Page.getCurrent().reload();
            getSession().close();
        });
        logout.setId("nav-logout");
        logout.addStyleName("link");
        logout.setIcon(VaadinIcons.SIGN_OUT);

        addComponentsAndExpand(logout);
        logout.setWidth(null);
        setComponentAlignment(logout, Alignment.MIDDLE_RIGHT);
    }


    @Override
    public void detach() {
        super.detach();
        registration.remove();
    }
}
