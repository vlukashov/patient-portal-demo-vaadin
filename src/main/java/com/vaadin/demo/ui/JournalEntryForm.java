package com.vaadin.demo.ui;

import com.vaadin.data.BeanBinder;
import com.vaadin.data.HasValue;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.demo.entities.*;
import com.vaadin.demo.repositories.DoctorRepository;
import com.vaadin.demo.repositories.PatientRepository;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Date;

/**
 * Created by mstahv
 */
@SpringComponent
@UIScope
public class JournalEntryForm extends SubView {

    @Autowired
    PatientRepository repo;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    @Lazy
    PatientDetails patientDetails;

    @Autowired
    @Lazy
    PatientView patientView;

    private Label patient = new Label();
    private DateField date = new DateField("Date");
    private NativeSelect<AppointmentType> type = new NativeSelect<>("Appointment");
    private ComboBox<Doctor> doctor = new ComboBox<>("Doctor");
    private TextArea entry = new TextArea();

    private Button save = new Button("Save");
    private Button cancel = new Button("Cancel");
    private Button secondaryCancel = new Button(FontAwesome.TIMES);

    BeanBinder<JournalEntry> bb = new BeanBinder<>(JournalEntry.class);

    @PostConstruct
    public void init() {
        VerticalLayout layout = new VerticalLayout();
        layout.setCaption("Edit Patient");
        layout.setSizeFull();
        FormLayout formlayout = new FormLayout(patient, date, type, doctor);
        formlayout.setMargin(true);
        layout.addComponent(formlayout);

        Label l = new Label("NOTES");
        layout.addComponent(new VerticalLayout(l, entry));
        final HorizontalLayout horizontalLayout = new HorizontalLayout(save, cancel);
        horizontalLayout.setSpacing(true);
        layout.addComponent(horizontalLayout);

        type.setItems(AppointmentType.values());
        doctor.setItems(doctorRepository.findAll());

        save.addClickListener(e -> {
            Patient p = bb.getBean().get().getPatient();
            repo.save(p);
            close();
            // For better separation of concern, one should consider using
            // events to communicate between ui components
            patientDetails.showPatient(p);
            patientView.listPatients();
        });
        
        cancel.addClickListener(e -> close());
        
        addTab(layout);

        secondaryCancel.addClickListener(e-> close());
        setTopRightComponent(secondaryCancel);

        bb.forField(date).withConverter(localDate -> {
            return new Date();
        }, date -> {
            return LocalDate.now();
        }).bind("date");

        bb.bindInstanceFields(this);
        
    }
    
    void editJournal(JournalEntry j) {

        bb.setBean(j);
        show();
    }
    
}
