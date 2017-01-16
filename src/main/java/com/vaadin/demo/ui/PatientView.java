package com.vaadin.demo.ui;

import com.vaadin.demo.entities.Patient;
import com.vaadin.demo.repositories.PatientRepository;
import com.vaadin.demo.ui.mobile.MobilePatientListing;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

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

    @Autowired
    @Lazy
    private MobilePatientListing mobilePatientListing;

    @Autowired
    public PatientView(PatientRepository repository, PatientDetails patientDetails) {
        this.repo = repository;
        this.patientDetails = patientDetails;
    }

    @PostConstruct
    void init() {
    }

    @Override
    public void attach() {
        super.attach();

        buildLayout();
    }

    private void buildLayout() {
        if (((VaadinUI) UI.getCurrent()).getLayoutMode() == LayoutMode.DESKTOP) {

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
        } else {

            addComponent(mobilePatientListing);
            listPatients();
        }
    }

    public void listPatients() {
        repo.flush();
        if (((VaadinUI) UI.getCurrent()).getLayoutMode() == LayoutMode.DESKTOP) {
            patients.setItems(repo.findAll());
        } else {
            mobilePatientListing.list();
        }
    }

    public void focusPatient(Patient p) {
        patientDetails.showPatient(p);
    }

    @Override
    public void subViewClose() {
        if (patients != null)
            patients.getSelectionModel().deselectAll();
    }

    @Override
    public void repaint() {
        removeAllComponents();
        buildLayout();
    }
}
