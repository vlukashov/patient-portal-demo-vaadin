package com.vaadin.demo.ui.service;

import com.vaadin.demo.entities.JournalEntry;
import com.vaadin.demo.entities.Patient;
import com.vaadin.demo.repositories.PatientRepository;
import com.vaadin.demo.service.PatientService;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import io.reactivex.subjects.BehaviorSubject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@SpringComponent
@ViewScope
public class PatientsService {

    private BehaviorSubject<List<Patient>> patients = BehaviorSubject.create();
    private BehaviorSubject<Optional<Patient>> currentPatient = BehaviorSubject.create();
    private PatientRepository repo;

    private PatientService patientService;

    @Autowired
    PatientsService(PatientRepository repo, PatientService patientService) {
        this.repo = repo;
        this.patientService = patientService;
    }

    public BehaviorSubject<Optional<Patient>> getCurrentPatient() {
        if (!currentPatient.hasValue()) {
            currentPatient.onNext(Optional.empty());
        }
        return currentPatient;
    }

    public BehaviorSubject<List<Patient>> getPatients() {
        if (!patients.hasValue()) {
            patients.onNext(repo.findAll());
        }

        return patients;
    }

    public void selectPatient(Long id) {
        if (currentPatient.getValue().isPresent()) {
            currentPatient.getValue().ifPresent(p -> {
                if (!p.getId().equals(id)) {
                    currentPatient.onNext(Optional.of(repo.findOne(id)));
                }
            });
        } else if(id != null) {
            Patient patient = repo.findOne(id);
            if(patient == null) {
                System.err.println("Failed to find patient with ID " + id);
            }
            currentPatient.onNext(Optional.ofNullable(patient));
        } else {
            currentPatient.onNext(Optional.empty());
        }
    }

    public void savePatient(Patient p) {
        currentPatient.onNext(Optional.of(repo.save(p)));
        patients.onNext(repo.findAll());
    }

    public void deleteCurrentPatient() {
        currentPatient.getValue().ifPresent(p -> repo.delete(p.getId()));
        patients.onNext(repo.findAll());
        currentPatient.onNext(Optional.empty());
    }

    public void addJournalEntry(JournalEntry entry) {
        currentPatient.getValue().ifPresent(patient -> {
            Patient attached = patientService.findAttached(patient);
            attached.getJournalEntries().add(0, entry);
            savePatient(attached);
        });
    }
}
