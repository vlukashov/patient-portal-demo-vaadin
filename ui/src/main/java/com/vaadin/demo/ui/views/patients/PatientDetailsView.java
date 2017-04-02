package com.vaadin.demo.ui.views.patients;

import com.vaadin.demo.ui.service.PatientsService;
import com.vaadin.demo.ui.views.base.CssLayoutView;
import com.vaadin.demo.ui.views.patients.journal.JournalEditView;
import com.vaadin.demo.ui.views.patients.journal.JournalListingView;
import com.vaadin.demo.ui.views.patients.profile.ProfileEditView;
import com.vaadin.demo.ui.views.patients.profile.ProfileView;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.CssLayout;
import org.springframework.beans.factory.annotation.Autowired;


@SpringComponent
@ViewScope
public class PatientDetailsView extends CssLayoutView {

    private final PatientsService patientsService;
    private final SubViewNavigator navigator;
    private CssLayout content;


    @Autowired
    public PatientDetailsView(PatientsService patientsService, SubViewNavigator navigator, ProfileView profileView, ProfileEditView profileEditView, JournalListingView journalListingView, JournalEditView journalEditView) {
        this.navigator = navigator;
        addStyleName("patient-details-view");

        this.patientsService = patientsService;
        content = new CssLayout();

        SubNavBar navBar = new SubNavBar(navigator, patientsService);
        addComponent(navBar);
        content.setSizeFull();
        addComponent(content);

        navigator.addView(ProfileView.VIEW_NAME, profileView);
        navigator.addView(ProfileEditView.VIEW_NAME, profileEditView);
        navigator.addView(JournalListingView.VIEW_NAME, journalListingView);
        navigator.addView(JournalEditView.VIEW_NAME, journalEditView);
        navigator.addView("new", profileEditView);
        navigator.setFallback(ProfileView.VIEW_NAME);

        navigator.viewChanges().subscribe(view -> {
            content.removeAllComponents();
            content.addComponent(view);
            Page.getCurrent().setTitle(view.getTitle());
            navBar.setVisible(!view.isFullScreen());
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


    public void initFromParams(String prefix, String parameters) {
        navigator.init(prefix, parameters);
    }
}
