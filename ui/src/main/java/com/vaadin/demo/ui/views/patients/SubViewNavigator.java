package com.vaadin.demo.ui.views.patients;

import com.vaadin.demo.ui.views.patients.journal.JournalEditView;
import com.vaadin.demo.ui.views.patients.journal.JournalListingView;
import com.vaadin.demo.ui.views.patients.profile.ProfileEditView;
import com.vaadin.demo.ui.views.patients.profile.ProfileView;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;

@SpringComponent
public class SubViewNavigator {
    private final PatientsService patientsService;
    private final Disposable subscription;
    private Set<SubView> views = new HashSet<>();
    private PublishSubject<SubView> viewSubject = PublishSubject.create();

    private String prefix;
    private Long id;
    private String subViewUrl;


    @Autowired
    SubViewNavigator(PatientsService patientsService) {
        this.patientsService = patientsService;

        subscription = patientsService.getCurrentPatient().subscribe(p -> {
            p.ifPresent(patient -> id = patient.getId());
        });
    }

    public void addViews(SubView... subViews) {
        views.addAll(Arrays.asList(subViews));
    }

    public void navigateTo(String url) {
        views.stream().filter(v -> v.getUrl().equals(url)).findFirst().ifPresent(view -> viewSubject.onNext(view));
        Page.getCurrent().setUriFragment(prefix + "/" + id + "/" + url, false);
    }

    public void close() {
        patientsService.getCurrentPatient().onNext(Optional.empty());
    }

    public PublishSubject<SubView> getViewSubject() {
        return viewSubject;
    }

    public void initFromUri() {
        List<String> parts = new LinkedList<>(Arrays.asList(Page.getCurrent().getUriFragment().split("/")));


        if (parts.size() > 0) {
            prefix = parts.get(0);
            parts.remove(0);
        }

        if (parts.size() > 0) {
            id = Long.valueOf(parts.get(0));
            parts.remove(0);
            patientsService.selectPatient(id);
        }

        if (parts.size() > 0) {
            navigateTo(String.join("/", parts));
        }
    }

    @PreDestroy
    void unsubscribe() {
        subscription.dispose();
    }
}
