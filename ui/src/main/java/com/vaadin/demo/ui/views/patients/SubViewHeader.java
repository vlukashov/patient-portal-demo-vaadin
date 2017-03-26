package com.vaadin.demo.ui.views.patients;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;


public class SubViewHeader extends HorizontalLayout {

    public SubViewHeader(PatientDetailsView.SubViewNavigator navigator, String heading, String closeLink) {
        addStyleName("sub-view-header");

        Label text = new Label(heading);
        text.addStyleName(ValoTheme.LABEL_H1);
        Button close = new Button();
        close.addClickListener(click -> navigator.navigateTo(closeLink));
        close.setIcon(VaadinIcons.CLOSE);
        close.addStyleName(ValoTheme.BUTTON_ICON_ONLY);

        setWidth("100%");
        addComponents(text, close);
        setExpandRatio(text, 1);
        setComponentAlignment(text, Alignment.MIDDLE_CENTER);
    }
}
