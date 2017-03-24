package com.vaadin.demo.ui.views.patients;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;


@SpringComponent
public class PatientDetailsView extends VerticalLayout {

    @Autowired
    PatientSubject patientSubject;

    public PatientDetailsView() {
        addStyleName("patient-details-view");
        addComponent(new Label("This is details view"));
    }

    //init from params, reflect changes to url
    void init() {
        registerSubViews();
        buildLayout();
        patientSubject.get().subscribe(maybePatient -> {
            setVisibility(maybePatient.isPresent());
        });
    }

    private void registerSubViews() {

    }

    private void buildLayout() {

    }

    private void setVisibility(boolean hasPatient) {
        if (hasPatient) {
            addStyleName("open");
        } else {
            removeStyleName("open");
        }
    }
}
