package com.vaadin.demo.ui.views.patients.profile;


import com.vaadin.demo.ui.views.patients.SubView;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
public class ProfileView extends VerticalLayout implements SubView {

    @Override
    public String getUrl() {
        return "profile";
    }

    @Override
    public String getTitle() {
        return "Patient profile";
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void enter() {

    }

    @Override
    public void exit() {

    }
}
