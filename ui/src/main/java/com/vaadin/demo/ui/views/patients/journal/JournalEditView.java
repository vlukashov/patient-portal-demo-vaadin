package com.vaadin.demo.ui.views.patients.journal;


import com.vaadin.demo.ui.views.patients.SubView;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
public class JournalEditView extends VerticalLayout implements SubView {

    @Override
    public String getUrl() {
        return "journal/new";
    }

    @Override
    public String getTitle() {
        return "Add Patient Journal Entry";
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

}
