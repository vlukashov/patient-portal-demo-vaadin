package com.vaadin.demo.ui;

import com.vaadin.demo.entities.JournalEntry;
import com.vaadin.demo.entities.Patient;
import com.vaadin.demo.repositories.PatientRepository;
import com.vaadin.demo.service.PatientService;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

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

    Button editBtn = new Button("EDIT");
    Button addBtn = new Button("ADD");
    Button back = new Button("ALL PATIENTS", FontAwesome.ARROW_LEFT);
    private Patient patient;

    public PatientDetails() {
        profile.setCaption("Profile");
        profile.setSpacing(true);
        profile.setMargin(true);
        addTab(profile);

        journal.setCaption("Journal");
        journal.setSpacing(true);
        journal.setMargin(true);
        addTab(journal);

        editBtn.addClickListener(e -> edit());
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
        setTopLeftComponent(back);

    }

    void showPatient(Patient p) {
        if(p.isPersistent()) {
            p = patientService.findAttached(p); // fetch with joins to history etc
        }
        patient = p;

        profile.removeAllComponents();

        Label fn = new Label(p.getFirstName());
        fn.setCaption("FIRST NAME");
        fn.setStyleName(ValoTheme.LABEL_H3);
        Label mn = new Label(p.getMiddleName());
        mn.setCaption("MIDDLE NAME");
        mn.setStyleName(ValoTheme.LABEL_H3);
        Label ln = new Label(p.getLastName());
        ln.setCaption("LAST NAME");
        ln.setStyleName(ValoTheme.LABEL_H3);
        HorizontalLayout nameLayout = new HorizontalLayout(fn, mn, ln);
        nameLayout.setSpacing(true);
        profile.addComponent(nameLayout);

        // TODO BIND data, manually is the only option for read only?
        FormLayout fl = new FormLayout();
        fl.addComponent(label("Gender", p.getGender()));
        fl.addComponent(label("Date of birth", p.getBirthDate()));
        fl.addComponent(label("Snn", p.getSsn()));
        fl.addComponent(label("Patient ID", p.getId()));
        fl.addComponent(label("Doctor", p.getDoctor()));
        fl.addComponent(label("Medical record", p.getMedicalRecord()));
        fl.addComponent(label("Last visit", p.getLastVisit()));

        profile.addComponent(fl);


        journal.removeAllComponents();

        Grid<JournalEntry> journalEntryGrid = new Grid<>();
        journalEntryGrid.setCaption(patient.toString());
        journalEntryGrid.addColumn("Date", j -> SimpleDateFormat.getDateInstance().format(j.getDate()));
        journalEntryGrid.addColumn("Appointment", j -> j.getAppointmentType().toString());
        journalEntryGrid.addColumn("Doctor", j -> j.getDoctor().toString());
        journalEntryGrid.addColumn("Notes", JournalEntry::getEntry).setWidth(200); // TODO how to set expand ratio + overflow or set relative width??
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

        show();
    }

    private Label label(String caption, Object value) {
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
