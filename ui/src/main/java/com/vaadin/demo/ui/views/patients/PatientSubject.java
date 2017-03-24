package com.vaadin.demo.ui.views.patients;

import com.vaadin.demo.entities.Patient;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import io.reactivex.subjects.BehaviorSubject;

import javax.annotation.PostConstruct;
import java.util.Optional;

@SpringComponent
public class PatientSubject {

    private BehaviorSubject<Optional<Patient>> patientSubject;

    @PostConstruct
    void init() {
        patientSubject = BehaviorSubject.create();
    }


    BehaviorSubject<Optional<Patient>> get() {
        return patientSubject;
    }


}
