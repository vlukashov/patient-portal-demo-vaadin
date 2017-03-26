package com.vaadin.demo.ui.views.patients.profile;

import com.vaadin.demo.ui.views.patients.SubView;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
public class ProfileEditView extends VerticalLayout implements SubView {
    @Override
    public String getUrl() {
        return "profile/edit";
    }

    @Override
    public String getTitle() {
        return "Editing Patient";
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

}
