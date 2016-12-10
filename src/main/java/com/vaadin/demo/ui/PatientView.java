package com.vaadin.demo.ui;

import com.vaadin.demo.entities.Patient;
import com.vaadin.demo.repositories.PatientRepository;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;

import javax.annotation.PostConstruct;


/**
 * Created by mstahv
 */
@SpringComponent
@UIScope
public class PatientView extends MainView {

    public static final String ACTIONS_COLUMN_ID = "actions";
    private final PatientRepository repo;
    private final PatientDetails patientDetails;

    private Grid<Patient> patients;
    private Button newPatientBtn;


    public PatientView(PatientRepository repository, PatientDetails patientDetails) {
        this.repo = repository;
        this.patientDetails = patientDetails;
    }

    @PostConstruct
    void init() {
        patients = new Grid<>();
        patients.setSizeFull();
        patients.addColumn("Name", patient->patient.getFirstName() + " " + patient.getLastName());
        patients.addColumn("Id", patient->patient.getId().toString());
        patients.addColumn("Medical record", patient->patient.getMedicalRecord().toString());
        patients.addColumn("Doctor", patient->patient.getDoctor().getFirstName() + " " + patient.getDoctor().getLastName());
        patients.addColumn("Last visit", patient-> {return  (patient.getLastVisit() == null) ? "" : patient.getLastVisit().toString();});
        
        // TODO how the hell should I create to buttons, other with "DANGER" + confirm dialog, in one column, for real

        ButtonRenderer<Patient> br = new ButtonRenderer<>( e -> {
            Patient p = e.getItem();
            focusPatient(p);
        });
        patients.addColumn(p->"editBtn", br).setCaption("");
        patients.addColumn(p->"delete", new ButtonRenderer<>(e->deletePatient(e.getItem()))).setCaption("");
        listPatients();

        newPatientBtn = new Button("Add patient");
        newPatientBtn.setIcon(FontAwesome.PLUS);
        newPatientBtn.addClickListener(e -> {
            Patient patient = new Patient();
            patientDetails.showPatient(patient);
            patientDetails.edit();
        });

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.addComponents(newPatientBtn, patients);
        layout.setComponentAlignment(newPatientBtn, Alignment.TOP_RIGHT);
        layout.setExpandRatio(patients, 1);
        
        layout.setSizeFull();
        
        addComponent(layout);
        
    }

    public void listPatients() {
        patients.setItems(repo.findAll());
    }

    private void focusPatient(Patient p) {
        patientDetails.showPatient(p);
    }

    private void deletePatient(Patient patient) {
        Window window = new Window();
        window.setCaption("Are you sure?");
        window.setClosable(false);
        window.setResizable(false);
        window.setModal(true);
        Label label = new Label("You are about to delete patient details of " + patient + ".");

        Button delete = new Button("Delete");
        delete.setStyleName(ValoTheme.BUTTON_DANGER);
        delete.addClickListener(e-> {
            repo.delete(patient);
            window.close();
        });

        Button cancel = new Button("Cancel");
        cancel.addClickListener(e-> {
            window.close();
        });
        HorizontalLayout actions = new HorizontalLayout(cancel, delete);
        actions.setSpacing(true);

        VerticalLayout layout = new VerticalLayout(label, actions);
        layout.setMargin(true);
        layout.setSpacing(true);

        window.setContent(layout);

        getUI().addWindow(window);

    }

}
