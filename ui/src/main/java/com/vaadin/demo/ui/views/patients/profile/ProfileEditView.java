package com.vaadin.demo.ui.views.patients.profile;

import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.ValidationException;
import com.vaadin.demo.entities.Doctor;
import com.vaadin.demo.entities.Gender;
import com.vaadin.demo.entities.Patient;
import com.vaadin.demo.repositories.DoctorRepository;
import com.vaadin.demo.ui.converters.DateConverter;
import com.vaadin.demo.ui.converters.LongConverter;
import com.vaadin.demo.ui.service.PatientsService;
import com.vaadin.demo.ui.views.base.VerticalLayoutView;
import com.vaadin.demo.ui.views.patients.SubView;
import com.vaadin.demo.ui.views.patients.SubViewHeader;
import com.vaadin.demo.ui.views.patients.SubViewNavigator;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Map;

@SpringComponent
@ViewScope
public class ProfileEditView extends VerticalLayoutView implements SubView {
    public static final String VIEW_NAME = ":id/profile/edit";
    private DoctorRepository doctorRepository;
    private PatientsService patientsService;
    private SubViewNavigator navigator;
    private BeanValidationBinder<Patient> binder;
    private NativeButton deleteButton;
    private Patient patient;

    @Autowired
    public ProfileEditView(DoctorRepository doctorRepository, PatientsService patientsService, SubViewNavigator navigator) {
        addStyleName("profile-edit-view");
        this.doctorRepository = doctorRepository;
        this.patientsService = patientsService;
        this.navigator = navigator;
    }

    @PostConstruct
    void init() {
        setMargin(false);
        buildLayout();
    }

    @Override
    public void enter(Map<String, String> params) {
        if (params.containsKey("id")) {
            patientsService.selectPatient(Long.valueOf(params.get("id")));
        } else {
            updateFromPatient(new Patient());
        }
    }

    @Override
    public void attach() {
        super.attach();
        addSubscription(patientsService.getCurrentPatient().subscribe(p -> p.ifPresent(this::updateFromPatient)));
    }

    private void updateFromPatient(Patient patient) {
        this.patient = patient;

        binder.setBean(patient);
        deleteButton.setVisible(patient.getId() != null);
    }

    private void buildLayout() {
        setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        SubViewHeader header = new SubViewHeader(navigator, getTitle(), close -> backOrClose());
        PatientFormLayout formLayout = new PatientFormLayout();
        formLayout.setWidth("80%");

        binder = new BeanValidationBinder<>(Patient.class);
        binder.forField(formLayout.birthDate).withConverter(new DateConverter()).bind("birthDate");
        binder.forField(formLayout.medicalRecord).withConverter(new LongConverter()).bind("medicalRecord");
        binder.bindInstanceFields(formLayout);


        NativeButton saveButton = new NativeButton("Save");
        saveButton.setId("save-nativebutton");
        saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        NativeButton cancelButton = new NativeButton("Cancel");
        cancelButton.setId("cancel-nativebutton");
        deleteButton = new NativeButton("Delete");
        deleteButton.setId("delete-nativebutton");
        deleteButton.addStyleName(ValoTheme.BUTTON_DANGER);

        saveButton.addClickListener(click -> {
            try {
                binder.writeBean(binder.getBean());
                patientsService.savePatient(binder.getBean());
                navigator.navigateToPath(ProfileView.VIEW_NAME);
            } catch (ValidationException e) {
                Notification.show("Save failed", Notification.Type.WARNING_MESSAGE);
            }
        });

        cancelButton.addClickListener(click -> {
            backOrClose();
        });
        deleteButton.addClickListener(click -> {
            patientsService.deleteCurrentPatient();
        });

        addComponents(header, formLayout, new HorizontalLayout(saveButton, cancelButton, deleteButton));
        formLayout.firstName.focus();
    }

    private void backOrClose() {
        if(patient.getId() != null) {
            navigator.navigateToPath(ProfileView.VIEW_NAME);
        } else {
            navigator.close();
        }
    }

    @Override
    public String getTitle() {
        return "Editing Patient";
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }


    class PatientFormLayout extends FormLayout {

        private final ComboBox<String> title;
        private final TextField firstName;
        private final TextField middleName;
        private final TextField lastName;
        private final ComboBox<Gender> gender;
        private final DateField birthDate;
        private final TextField ssn;
        private final ComboBox<Doctor> doctor;
        private final TextField medicalRecord;

        PatientFormLayout() {

            title = new ComboBox<>("Title");
            title.setId("title-combobox");
            title.setItems("Miss", "Ms", "Mrs", "Mr");
            title.setEmptySelectionAllowed(false);

            firstName = new TextField("First Name");
            firstName.setValueChangeMode(ValueChangeMode.BLUR);
            firstName.setId("firstname-textfield");
            middleName = new TextField("Middle Name");
            middleName.setValueChangeMode(ValueChangeMode.BLUR);
            middleName.setId("middlename-textfield");
            lastName = new TextField("Last Name");
            lastName.setValueChangeMode(ValueChangeMode.BLUR);
            lastName.setId("lastname-textfield");

            gender = new ComboBox<>("Gender");
            gender.setId("gender-combobox");
            gender.setItems(Gender.values());
            gender.setEmptySelectionAllowed(false);

            birthDate = new DateField("Date of Birth");
            birthDate.setId("birthdate-datefield");
            medicalRecord = new TextField("Medical Record");
            medicalRecord.setValueChangeMode(ValueChangeMode.BLUR);
            medicalRecord.setId("medical-record-textfield");
            ssn = new TextField("SSN");
            ssn.setValueChangeMode(ValueChangeMode.BLUR);
            ssn.setId("ssn-textfield");

            doctor = new ComboBox<>("Doctor");
            doctor.setId("doctor-combobox");
            doctor.setItems(doctorRepository.findAll());
            doctor.setEmptySelectionAllowed(false);

            addComponents(title, firstName, middleName, lastName, gender, birthDate, medicalRecord, ssn, doctor);
            iterator().forEachRemaining(c -> c.setWidth("100%"));
        }
    }

}
