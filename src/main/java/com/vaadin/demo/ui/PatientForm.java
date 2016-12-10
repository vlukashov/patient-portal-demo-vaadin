package com.vaadin.demo.ui;

import com.vaadin.data.BeanBinder;
import com.vaadin.data.HasValue;
import com.vaadin.demo.entities.Gender;
import com.vaadin.demo.entities.Patient;
import com.vaadin.demo.repositories.PatientRepository;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;

import java.time.LocalDate;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * Created by mstahv
 */
@SpringComponent
@UIScope
public class PatientForm extends SubView {
    
    @Autowired
    PatientRepository repo;

    @Autowired
    @Lazy
    PatientDetails patientDetails;

    @Autowired
    @Lazy
    PatientView patientView;
    
    private TextField firstName = new TextField("First name");
    private TextField middleName = new TextField("Middle name");
    private TextField lastName = new TextField("Last name");
    private NativeSelect<String> greeting = new NativeSelect<>("Greeting");
    private NativeSelect<Gender> gender = new NativeSelect<>("Gender");
    private DateField birthDate = new DateField("Date of birth");
    private TextField ssn = new TextField("SSN");
    private TextField medicalRecord = new TextField("Patient ID");
    
    private Button save = new Button("Save");
    private Button cancel = new Button("Cancel");
    private Button secondaryCancel = new Button(FontAwesome.TIMES);
    
    BeanBinder<Patient> bb = new BeanBinder<>(Patient.class);
    
    public PatientForm() {
        CssLayout layout = new CssLayout();
        layout.setCaption("Edit Patient");
        layout.setSizeFull();
        FormLayout formlayout = new FormLayout(firstName, middleName, lastName, greeting, gender, birthDate, ssn, medicalRecord);
        formlayout.setMargin(true);
        final HorizontalLayout horizontalLayout = new HorizontalLayout(save, cancel);
        horizontalLayout.setSpacing(true);
        formlayout.addComponent(horizontalLayout);
        layout.addComponent(formlayout);
        
        gender.setItems(Gender.values());
        
        medicalRecord.setReadOnly(true);

        bb.forField(firstName)
                .withNullRepresentation("")//.setRequired("ÖÖÖ let me have this message from JSR 303")

                .bind("firstName");

        bb.forField(birthDate).withConverter(localDate -> {
            return new Date();
        }, date -> {
            return LocalDate.now();
        }).bind("birthDate");
        
        bb.forField(medicalRecord).withConverter(Long::valueOf, String::valueOf).bind("medicalRecord");
        
        bb.bind(gender, "gender");
        
        HasValue.ValueChangeListener<String> updateGreetings = e -> {
            greeting.setItems(firstName.getValue(), middleName.getValue());
        };
        firstName.addValueChangeListener(updateGreetings);
        middleName.addValueChangeListener(updateGreetings);
        
        
        save.addClickListener(e -> {
            repo.save(bb.getBean().get());
            close();
            // For better separation of concern, one should consider using
            // events to communicate between ui components
            patientDetails.showPatient(bb.getBean().get());
            patientView.listPatients();
        });
        
        cancel.addClickListener(e -> close());
        
        addTab(layout);

        secondaryCancel.addClickListener(e-> close());
        setTopRightComponent(secondaryCancel);
        
        bb.bindInstanceFields(this);

        bb.addStatusChangeListener(e-> {
            Notification.show("Contains errors:" + e.hasValidationErrors(), Notification.Type.TRAY_NOTIFICATION);
        });
        
    }
    
    void editPatient(Patient p) {
        if(p.isPersistent()) {
            p = repo.findOne(p.getId());
        }
        bb.setBean(p);
        show();
    }
    
}
