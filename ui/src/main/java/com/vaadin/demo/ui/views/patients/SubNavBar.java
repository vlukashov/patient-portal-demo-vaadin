package com.vaadin.demo.ui.views.patients;

import com.vaadin.demo.ui.views.base.NavBar;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;

class SubNavBar extends NavBar {

    SubNavBar(PatientDetailsView.SubViewNavigator navigator) {
        addStyleName("sub-nav-bar");
        setWidth("100%");

        NativeButton backButton = new NativeButton("All patients", click -> navigator.close());
        backButton.setIcon(VaadinIcons.ARROW_LONG_LEFT);

        HorizontalLayout subPagesLayout = new HorizontalLayout();
        subPagesLayout.addStyleName("sub-pages");

        navButtons.put("profile", new NativeButton("Profile", click -> navigator.navigateTo("profile")));
        navButtons.put("journal", new NativeButton("Journal", click -> navigator.navigateTo("journal")));

        navButtons.forEach((name, button) -> {
            subPagesLayout.addComponent(button);
        });

        NativeButton editButton = new NativeButton("Edit Patient");
        addComponents(backButton, subPagesLayout, editButton);

        setExpandRatio(subPagesLayout, 1);
        setComponentAlignment(subPagesLayout, Alignment.MIDDLE_CENTER);

    }
}
