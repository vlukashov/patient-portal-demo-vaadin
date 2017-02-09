package com.vaadin.demo.ui;

import com.vaadin.demo.entities.Patient;
import com.vaadin.demo.repositories.PatientRepository;
import com.vaadin.demo.ui.mobile.MobilePatientListing;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;


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

    private VerticalLayout patientLayout;

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
            if (patientLayout == null) {
                buildPatientLayout();
            }

            // add patients to gird
            listPatients();

            addComponent(patientLayout);
        } else {

            addComponent(mobilePatientListing);
            listPatients();
        }
    }

    // Build desktop patient layout.
    private void buildPatientLayout() {
        // Setup patient grid if not already initiated.
        if (patients == null) {
            setupPatientGrid();
        }

        if (newPatientBtn == null) {
            initNewPatientButton();
        }

        patientLayout = new VerticalLayout();
        patientLayout.setSpacing(true);
        patientLayout.setMargin(true);
        patientLayout.addComponents(newPatientBtn, patients);
        patientLayout.setComponentAlignment(newPatientBtn, Alignment.TOP_RIGHT);
        patientLayout.setExpandRatio(patients, 1);

        patientLayout.setSizeFull();
    }

    private void setupPatientGrid() {
        patients = new Grid<>();
        patients.setSelectionMode(Grid.SelectionMode.SINGLE);

        patients.setHeight("100%");
        patients.setSizeFull();
        patients.addColumn(patient -> patient.getFirstName() + " " + patient.getLastName()).setId("name").setCaption("Name");
        patients.addColumn(patient -> patient.getId().toString()).setId("id").setCaption("Id");
        patients.addColumn(patient -> patient.getMedicalRecord().toString()).setId("medicalRecord").setCaption("Medical record");
        patients.addColumn(patient -> patient.getDoctor().getFirstName() + " " + patient.getDoctor().getLastName()).setId("doctor").setCaption("Doctor");
        patients.addColumn(patient ->
                patient.getLastVisit() == null ? "" : SimpleDateFormat.getDateInstance().format(patient.getLastVisit())
        ).setId("lastVisit").setCaption("Last visit");

        patients.addSelectionListener(e -> {
            Patient value = patients.asSingleSelect().getValue();
            if (value != null)
                focusPatient(value);
        });
    }

    private void initNewPatientButton() {
        newPatientBtn = new Button("Add new patient");
        newPatientBtn.setIcon(FontAwesome.PLUS); // Planned to not replaced!
        newPatientBtn.addStyleName("addButton");
        newPatientBtn.addClickListener(e -> {
            focusPatient(new Patient());
            patientDetails.edit();
        });
    }

    public void listPatients() {
        // Flush to get clean list from repository.
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
