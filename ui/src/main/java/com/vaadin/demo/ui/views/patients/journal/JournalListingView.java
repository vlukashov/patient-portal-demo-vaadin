package com.vaadin.demo.ui.views.patients.journal;


import com.vaadin.demo.entities.JournalEntry;
import com.vaadin.demo.entities.Patient;
import com.vaadin.demo.service.PatientService;
import com.vaadin.demo.ui.views.patients.PatientsService;
import com.vaadin.demo.ui.views.patients.SubView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import io.reactivex.disposables.Disposable;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
public class JournalListingView extends VerticalLayout implements SubView {


    final PatientsService patientsService;
    private PatientService patientService;
    private Grid<JournalEntry> journalGrid;
    private Disposable sub;
    private Label nameLabel;

    @Override
    public String getUrl() {
        return "journal";
    }

    @Override
    public String getTitle() {
        return "Patient Journal";
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }


    @Autowired
    public JournalListingView(PatientsService patientsService, PatientService patientService) {
        this.patientsService = patientsService;
        this.patientService = patientService;

        addHeaderLayout();
        addGrid();
    }

    private void addHeaderLayout() {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        nameLabel = new Label();
        nameLabel.addStyleName(ValoTheme.LABEL_H1);
        Button addButton = new Button("New Entry");
        addButton.setIcon(VaadinIcons.PLUS);
        addButton.addStyleName("addButton");
        headerLayout.addComponentsAndExpand(nameLabel);
        headerLayout.addComponent(addButton);
        addComponent(headerLayout);
    }

    private void addGrid() {
        journalGrid = new Grid<>(JournalEntry.class);
        journalGrid.setSizeFull();
        addComponentsAndExpand(journalGrid);
    }


    private void updateFromPatient(Patient patient) {
        journalGrid.setItems(patient.getJournalEntries());
        nameLabel.setValue(patient.getFirstName() + " " + patient.getLastName());
    }

    @Override
    public void attach() {
        super.attach();

        sub = patientsService.getCurrentPatient().subscribe(patient ->
                patient.ifPresent(p -> updateFromPatient(patientService.findAttached(p)))
        );
    }


    @Override
    public void detach() {
        super.detach();
        sub.dispose();
    }
}
