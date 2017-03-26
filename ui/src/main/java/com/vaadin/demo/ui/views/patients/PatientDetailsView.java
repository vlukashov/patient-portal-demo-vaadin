package com.vaadin.demo.ui.views.patients;

import com.vaadin.demo.repositories.PatientRepository;
import com.vaadin.demo.ui.views.base.CssLayoutView;
import com.vaadin.demo.ui.views.patients.journal.JournalEditView;
import com.vaadin.demo.ui.views.patients.journal.JournalListingView;
import com.vaadin.demo.ui.views.patients.profile.ProfileEditView;
import com.vaadin.demo.ui.views.patients.profile.ProfileView;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.CssLayout;
import io.reactivex.subjects.PublishSubject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@SpringComponent
public class PatientDetailsView extends CssLayoutView {

    private final PatientsService patientsService;
    private CssLayout content;


    @Autowired
    public PatientDetailsView(PatientRepository repo, PatientsService patientsService, ProfileView profileView, ProfileEditView profileEditView, JournalListingView journalListingView, JournalEditView journalEditView) {
        addStyleName("patient-details-view");

        this.patientsService = patientsService;
        SubViewNavigator navigator = new SubViewNavigator();
        navigator.addViews(profileView, profileEditView, journalListingView, journalEditView);

        SubNavBar navBar = new SubNavBar(navigator);
        addComponent(navBar);
        content = new CssLayout();
        content.setSizeFull();
        addComponent(content);

        navigator.getViewSubject().subscribe(view -> {
            content.removeAllComponents();
            content.addComponent(view);
            Page.getCurrent().setTitle(view.getTitle());
        });

        setSizeUndefined();
    }


    @Override
    public void attach() {
        super.attach();
        addSubscription(patientsService.getCurrentPatient().subscribe(maybePatient ->
                setVisibility(maybePatient.isPresent())));
    }

    private void setVisibility(boolean hasPatient) {
        if (hasPatient) {
            addStyleName("open");
        } else {
            removeStyleName("open");
        }
    }


    /**
     * Urls are of form prefix/{id|new}/sub-view-url
     */
    class SubViewNavigator {
        private Set<SubView> views = new HashSet<>();
        private String prefix;
        private PublishSubject<SubView> viewSubject = PublishSubject.create();

        SubViewNavigator() {

        }

        void addViews(SubView... subViews) {
            views.addAll(Arrays.asList(subViews));
        }

        void navigateTo(String url) {
            views.stream().filter(v -> v.getUrl().equals(url)).findFirst().ifPresent(view -> viewSubject.onNext(view));
        }


        void close() {
            patientsService.getCurrentPatient().onNext(Optional.empty());
        }

        public PublishSubject<SubView> getViewSubject() {
            return viewSubject;
        }
    }
}
