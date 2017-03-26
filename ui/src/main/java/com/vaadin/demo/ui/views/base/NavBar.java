package com.vaadin.demo.ui.views.base;

import com.vaadin.shared.Registration;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

import java.util.LinkedHashMap;
import java.util.Map;

public class NavBar extends HorizontalLayout {
    protected Map<String, Button> navButtons = new LinkedHashMap<>();
    private Registration registration;

    @Override
    public void attach() {
        super.attach();
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

    @Override
    public void detach() {
        super.detach();
        registration.remove();
    }
}
