package com.vaadin.demo.ui;

import com.vaadin.demo.entities.Patient;
import com.vaadin.demo.repositories.PatientRepository;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;


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
        patients.setSelectionMode(Grid.SelectionMode.SINGLE);

        patients.setHeight("100%");
        patients.setSizeFull();
        patients.addColumn(patient -> patient.getFirstName() + " " + patient.getLastName()).setId("name").setCaption("Name");
        patients.addColumn(patient -> patient.getId().toString()).setId("id").setCaption("Id");
        patients.addColumn(patient -> patient.getMedicalRecord().toString()).setId("medicalRecord").setCaption("Medical record");
        patients.addColumn(patient -> patient.getDoctor().getFirstName() + " " + patient.getDoctor().getLastName()).setId("doctor").setCaption("Doctor");
        patients.addColumn(patient -> {
            return (patient.getLastVisit() == null) ? "" : SimpleDateFormat.getDateInstance().format(patient.getLastVisit());
        }).setId("lastVisit").setCaption("Last visit");

        // TODO how the hell should I create to buttons, other with "DANGER" + confirm dialog, in one column, for real
//        ButtonRenderer<Patient> br = new ButtonRenderer<>( e -> {
//            Patient p = e.getItem();
//            focusPatient(p);
//        });
//        patients.addColumn(p->"editBtn", br).setCaption("");
//        patients.addColumn(p->"delete", new ButtonRenderer<>(e->deletePatient(e.getItem()))).setCaption("");

        listPatients();

        newPatientBtn = new Button("Add new patient");
        newPatientBtn.setIcon(FontAwesome.PLUS); // Planned to not replaced!
        newPatientBtn.addStyleName("addButton");
        newPatientBtn.addClickListener(e -> {
            focusPatient(new Patient());
            patientDetails.edit();
        });

        patients.addSelectionListener(e -> {
            Patient value = patients.asSingleSelect().getValue();
            if (value != null)
                focusPatient(value);
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
        delete.addClickListener(e -> {
            repo.delete(patient);
            window.close();
        });

        Button cancel = new Button("Cancel");
        cancel.addClickListener(e -> {
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

    @Override
    public void subViewClose() {
        patients.getSelectionModel().deselectAll();
    }
}
