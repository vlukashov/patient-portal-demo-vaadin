package com.vaadin.demo.ui.views.patients;

import com.vaadin.demo.ui.views.patients.journal.JournalEditView;
import com.vaadin.demo.ui.views.patients.journal.JournalListingView;
import com.vaadin.demo.ui.views.patients.profile.ProfileEditView;
import com.vaadin.demo.ui.views.patients.profile.ProfileView;
import com.vaadin.spring.annotation.SpringComponent;
import io.reactivex.subjects.PublishSubject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SpringComponent
public class SubViewNavigator {
    private final PatientsService patientsService;
    private Set<SubView> views = new HashSet<>();
    private PublishSubject<SubView> viewSubject = PublishSubject.create();


    @Autowired
    SubViewNavigator(PatientsService patientsService) {
        this.patientsService = patientsService;
    }

    public void addViews(SubView... subViews) {
        views.addAll(Arrays.asList(subViews));
    }

    public void navigateTo(String url) {
        views.stream().filter(v -> v.getUrl().equals(url)).findFirst().ifPresent(view -> viewSubject.onNext(view));
    }

    public void close() {
        patientsService.getCurrentPatient().onNext(Optional.empty());
    }

    public PublishSubject<SubView> getViewSubject() {
        return viewSubject;
    }
}
