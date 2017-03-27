package com.vaadin.demo.ui.views.patients;

import com.vaadin.demo.entities.Patient;
import com.vaadin.demo.ui.views.base.CssLayoutView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;

@SpringComponent
@SpringView(name = "patients")
public class PatientsView extends CssLayoutView implements View {

    private final PatientDetailsView detailsView;
    private final PatientsService patientsService;
    private Grid<Patient> patientsGrid;

    @Autowired
    public PatientsView(PatientDetailsView detailsView, PatientsService patientsService) {
        addStyleName("patients-view");
        this.detailsView = detailsView;
        this.patientsService = patientsService;
    }

    @PostConstruct
    void init() {
        setSizeFull();
        buildLayout();
        addComponent(detailsView);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("Patients");
    }

    @Override
    public void attach() {
        super.attach();
        addSubscription(patientsService.getPatients().subscribe(p -> {
            patientsGrid.setItems(p);
            patientsService.getCurrentPatient().getValue().ifPresent(selected -> patientsGrid.select(selected));
        }));
        addSubscription(patientsService.getCurrentPatient().subscribe(p -> p.ifPresent(patient -> patientsGrid.select(patient))));
    }

    private void buildLayout() {
        patientsGrid = new Grid<>();
        patientsGrid.setSizeFull();
        patientsGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        patientsGrid.addColumn(patient -> patient.getLastName() + ", " + patient.getFirstName()).setId("lastName").setCaption("Name");
        patientsGrid.addColumn(patient -> patient.getId().toString()).setId("id").setCaption("Id");
        patientsGrid.addColumn(patient -> patient.getMedicalRecord().toString()).setId("medicalRecord").setCaption("Medical record");
        patientsGrid.addColumn(patient -> patient.getDoctor().getLastName() + ", " + patient.getDoctor().getFirstName()).setId("doctor.lastName").setCaption("Doctor");
        patientsGrid.addColumn(patient ->
                patient.getLastVisit() == null ? "" : SimpleDateFormat.getDateInstance().format(patient.getLastVisit())
        ).setId("lastVisit").setCaption("Last visit");


        patientsGrid.addSelectionListener(e -> patientsService.getCurrentPatient().onNext(e.getFirstSelectedItem()));
        addComponent(patientsGrid);
    }
}
