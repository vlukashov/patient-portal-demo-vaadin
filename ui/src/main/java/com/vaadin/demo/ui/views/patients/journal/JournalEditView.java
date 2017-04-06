package com.vaadin.demo.ui.views.patients.journal;

import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.ValidationException;
import com.vaadin.demo.entities.AppointmentType;
import com.vaadin.demo.entities.Doctor;
import com.vaadin.demo.entities.JournalEntry;
import com.vaadin.demo.entities.Patient;
import com.vaadin.demo.repositories.DoctorRepository;
import com.vaadin.demo.ui.converters.DateConverter;
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
import java.util.Date;
import java.util.Map;

@SpringComponent
@ViewScope
public class JournalEditView extends VerticalLayoutView implements SubView {
    public static final String VIEW_NAME = ":id/journal/edit";
    private final DoctorRepository doctorRepository;
    private final PatientsService patientsService;
    private final SubViewNavigator navigator;
    private BeanValidationBinder<JournalEntry> binder;
    private JournalEditLayout editorLayout;

    @Autowired
    JournalEditView(DoctorRepository doctorRepository, PatientsService patientsService, SubViewNavigator navigator) {
        addStyleName("journal-edit-view");
        this.doctorRepository = doctorRepository;
        this.patientsService = patientsService;
        this.navigator = navigator;
    }

    @PostConstruct
    void init() {
        buildLayout();
    }

    private void buildLayout() {
        setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        SubViewHeader header = new SubViewHeader(navigator, getTitle(), close -> navigator.navigateToPath(JournalListingView.VIEW_NAME));
        editorLayout = new JournalEditLayout();
        NativeButton saveButton = new NativeButton("Save");
        saveButton.setId("journal-save-id");
        saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        NativeButton cancelButton = new NativeButton("Cancel");
        cancelButton.setId("journal-cancel-id");
        cancelButton.addStyleName(ValoTheme.BUTTON_DANGER);

        binder = new BeanValidationBinder<>(JournalEntry.class);
        binder.forField(editorLayout.date).withConverter(new DateConverter()).bind("date");
        binder.bindInstanceFields(editorLayout);

        saveButton.addClickListener(click -> {
            try {
                JournalEntry entry = binder.getBean();
                binder.writeBean(entry);
                patientsService.addJournalEntry(entry);
                navigator.navigateToPath(JournalListingView.VIEW_NAME);

            } catch (ValidationException e) {
                Notification.show("Save failed", Notification.Type.WARNING_MESSAGE);
            }
        });
        cancelButton.addClickListener(click -> navigator.navigateToPath(JournalListingView.VIEW_NAME));

        addComponents(header, editorLayout, new HorizontalLayout(saveButton, cancelButton));
    }

    @Override
    public void enter(Map<String, String> params) {
        if(params.containsKey("id")){
            patientsService.selectPatient(Long.valueOf(params.get("id")));
        }
    }

    @Override
    public void attach() {
        super.attach();
        addSubscription(patientsService.getCurrentPatient().subscribe(p -> p.ifPresent(this::updateFromPatient)));
    }

    private void updateFromPatient(Patient patient) {
        editorLayout.setPatient(patient);
        JournalEntry entry = new JournalEntry();
        entry.setPatient(patient);
        entry.setDate(new Date());
        binder.setBean(entry);
    }

    @Override
    public String getTitle() {
        return "New Journal Entry";
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    class JournalEditLayout extends VerticalLayout {

        final Label name;
        final DateField date;
        final ComboBox<AppointmentType> appointmentType;
        final ComboBox<Doctor> doctor;
        final TextArea entry;

        JournalEditLayout() {
            addStyleName("journal-edit-layout");
            name = new Label();
            name.setCaption("Name");
            date = new DateField("Date");
            appointmentType = new ComboBox<>("Appointment");
            appointmentType.setId("journal-appointment-type-combobox");
            appointmentType.setItems(AppointmentType.values());
            appointmentType.setEmptySelectionAllowed(false);
            doctor = new ComboBox<>("Doctor");
            doctor.setId("journal-doctor-combobox");
            doctor.setItems(doctorRepository.findAll());
            doctor.setEmptySelectionAllowed(false);

            entry = new TextArea("Notes");
            entry.setValueChangeMode(ValueChangeMode.BLUR);
            entry.setId("journal-entry");

            FormLayout innerForm = new FormLayout(name, date, appointmentType, doctor);
            addComponents(innerForm, entry);

            innerForm.iterator().forEachRemaining(c -> c.setWidth("100%"));
            iterator().forEachRemaining(c -> c.setWidth("100%"));

        }

        public void setPatient(Patient patient) {
            name.setValue(patient.getLastName() + ", " + patient.getFirstName());
        }
    }

}
