package com.vaadin.demo.ui.views.patients;

import com.vaadin.demo.entities.Patient;
import com.vaadin.demo.repositories.PatientRepository;
import com.vaadin.spring.annotation.SpringComponent;
import io.reactivex.subjects.BehaviorSubject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@SpringComponent
public class PatientsService {

    private BehaviorSubject<List<Patient>> patients = BehaviorSubject.create();
    private BehaviorSubject<Optional<Patient>> currentPatient = BehaviorSubject.create();
    private PatientRepository repo;

    @Autowired
    PatientsService(PatientRepository repo){
        this.repo = repo;
    }


    public BehaviorSubject<Optional<Patient>> getCurrentPatient() {
        return currentPatient;
    }

    public BehaviorSubject<List<Patient>> getPatients() {
        if (!patients.hasValue()) {
            patients.onNext(repo.findAll());
        }

        return patients;
    }

    public void selectPatient(Long id){
        currentPatient.onNext(Optional.of(repo.findOne(id)));
    }

    public void savePatient(Patient p){
        currentPatient.onNext(Optional.of(repo.save(p)));
        patients.onNext(repo.findAll());
    }
}
