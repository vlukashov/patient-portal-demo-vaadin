package com.vaadin.demo.ui;

import com.vaadin.demo.entities.JournalEntry;
import com.vaadin.demo.entities.Patient;
import com.vaadin.demo.repositories.PatientRepository;
import com.vaadin.demo.service.PatientService;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mstahv
 */
@SpringComponent
@UIScope
public class PatientDetails extends SubView {

    @Autowired
    PatientRepository repo;

    @Autowired
    PatientService patientService;

    VerticalLayout profile = new VerticalLayout();
    VerticalLayout journal = new VerticalLayout();

    @Autowired
    PatientForm form = new PatientForm();

    @Autowired
    JournalEntryForm journalEntryForm;

    Button editBtn = new Button("Edit Patient");
    Button addBtn = new Button("Add");
    Button back = new Button("All Patients", FontAwesome.ARROW_LEFT);

    private Patient patient, lastJournalPatient, lastProfilePatient;

    public PatientDetails() {
        getTabsheet().addSelectedTabChangeListener(event -> populate());

        profile.setCaption("Profile");
        profile.setSpacing(true);
        profile.setMargin(true);
        addTab(profile);

        journal.setCaption("Journal");
        journal.setSpacing(true);
        journal.setMargin(true);
        addTab(journal);

        editBtn.addClickListener(e -> edit());
        editBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        editBtn.addStyleName("uppercase");

        addBtn.addClickListener(e -> addJournal());
        setTopRightComponent(editBtn);
        getTabsheet().addSelectedTabChangeListener(e -> {
            if (e.getTabSheet().getSelectedTab() == journal) {
                setTopRightComponent(addBtn);
            } else {
                setTopRightComponent(editBtn);
            }

        });

        back.addClickListener(e -> close());
        back.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        back.addStyleName("uppercase");
        setTopLeftComponent(back);

    }

    void showPatient(Patient p) {
        if (p.isPersistent()) {
            p = patientService.findAttached(p); // fetch with joins to history etc
        }
        patient = p;

        populate();

        show();
    }

    private void populate() {
        if (patient == null) {
            return;
        }
        Component selectedTab = getTabsheet().getSelectedTab();
        if (selectedTab.equals(profile)) {
            populateProfile();
        } else {
            populateJournal();
        }
    }

    private void populateProfile() {
        if (patient.equals(lastProfilePatient)) {
            return;
        }
        profile.removeAllComponents();

        HorizontalLayout nameLayout = new HorizontalLayout();
        nameLayout.addComponent(getNameLabel(patient.getFirstName(), "First Name"));
        nameLayout.addComponent(getNameLabel(patient.getMiddleName(), "Middle Name"));
        nameLayout.addComponent(getNameLabel(patient.getLastName(), "Last Name"));
        nameLayout.setSpacing(true);

        profile.addComponent(nameLayout);

        // TODO BIND data, manually is the only option for read only?
        FormLayout fl = new FormLayout();
        fl.addStyleName("data-layout");
        fl.setMargin(false);
        fl.addComponent(createLabel("Gender", patient.getGender().toString().substring(0, 1) + patient.getGender().toString().substring(1).toLowerCase()));
        fl.addComponent(createLabel("Date of birth", patient.getBirthDate() == null ? "" : SimpleDateFormat.getDateInstance().format(patient.getBirthDate())));
        fl.addComponent(createLabel("Snn", patient.getSsn()));
        fl.addComponent(createLabel("Patient ID", patient.getId()));
        fl.addComponent(createLabel("Doctor", patient.getDoctor()));
        fl.addComponent(createLabel("Medical record", patient.getMedicalRecord()));
        fl.addComponent(createLabel("Last visit", patient.getLastVisit() == null ? "" : SimpleDateFormat.getDateInstance().format(patient.getLastVisit())));

        profile.addComponent(fl);
        lastProfilePatient = patient;
    }

    private void populateJournal() {
        if (patient.equals(lastJournalPatient)) {
            return;
        }
        journal.removeAllComponents();

        Grid<JournalEntry> journalEntryGrid = new Grid<>();
        journalEntryGrid.setCaption(patient.toString());
        journalEntryGrid.addColumn(j -> SimpleDateFormat.getDateInstance().format(j.getDate())).setCaption("Date");
        journalEntryGrid.addColumn(j -> j.getAppointmentType().toString()).setCaption("Appointment");
        journalEntryGrid.addColumn(j -> j.getDoctor().toString()).setCaption("Doctor").setExpandRatio(1);
        journalEntryGrid.addColumn(JournalEntry::getEntry).setCaption("Notes").setExpandRatio(1).setMaximumWidth(450); // TODO how to set expand ratio + overflow or set relative width??

        journalEntryGrid.setDetailsGenerator(j -> {
            Label l = new Label(j.getEntry());
            l.setWidth("100%");
            l.setCaption("NOTES");
            VerticalLayout vl = new VerticalLayout(l);
            vl.setMargin(true);
            return vl;
        });


        journalEntryGrid.addItemClickListener(e -> {
            journalEntryGrid.setDetailsVisible(e.getItem(), !journalEntryGrid.isDetailsVisible(e.getItem()));
        });

        journalEntryGrid.setItems(patient.getJournalEntries());

        journalEntryGrid.setWidth(100, Unit.PERCENTAGE);
        journal.addComponent(journalEntryGrid);

        lastJournalPatient = patient;
    }

    private Label getNameLabel(String content, String caption) {
        Label label = createLabel(caption, content);
        label.addStyleName("name-label");
        label.addStyleName("uppercase");
        return label;
    }

    private Label createLabel(String caption, Object value) {
        Label l = new Label(value == null ? "" : value.toString());
        l.setCaption(caption);
        return l;
    }

    private void addJournal() {
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setDate(new Date());
        journalEntry.setPatient(patient);
        patient.getJournalEntries().add(0, journalEntry);
        journalEntryForm.editJournal(journalEntry);
    }

    public void edit() {
        form.editPatient(patient);
    }
}
