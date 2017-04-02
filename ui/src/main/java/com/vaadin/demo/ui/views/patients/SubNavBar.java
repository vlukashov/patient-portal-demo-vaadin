package com.vaadin.demo.ui.views.patients;

import com.vaadin.demo.ui.service.PatientsService;
import com.vaadin.demo.ui.views.base.NavBar;
import com.vaadin.demo.ui.views.patients.journal.JournalEditView;
import com.vaadin.demo.ui.views.patients.journal.JournalListingView;
import com.vaadin.demo.ui.views.patients.profile.ProfileEditView;
import com.vaadin.demo.ui.views.patients.profile.ProfileView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import io.reactivex.disposables.Disposable;

import java.util.Optional;

class SubNavBar extends NavBar {

    private SubViewNavigator navigator;
    private Disposable subscription;

    SubNavBar(SubViewNavigator navigator, PatientsService patientsService) {
        this.navigator = navigator;
        addStyleName("sub-nav-bar");
        setWidth("100%");
        setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        NativeButton backButton = new NativeButton("All patients", click -> patientsService.getCurrentPatient().onNext(Optional.empty()));
        backButton.setIcon(VaadinIcons.ARROW_LONG_LEFT);
        backButton.addStyleName("back-button");

        HorizontalLayout subPagesLayout = new HorizontalLayout();
        subPagesLayout.addStyleName("sub-pages");

        navButtons.put(ProfileView.VIEW_NAME, new NativeButton("Profile", click -> navigator.navigateToPath(ProfileView.VIEW_NAME)));
        navButtons.put(JournalListingView.VIEW_NAME, new NativeButton("Journal", click -> navigator.navigateToPath(JournalListingView.VIEW_NAME)));

        navButtons.forEach((name, button) -> {
            subPagesLayout.addComponent(button);
        });

        NativeButton editButton = new NativeButton("Edit", click -> navigator.navigateToPath(ProfileEditView.VIEW_NAME));
        addComponents(backButton, subPagesLayout, editButton);

        setExpandRatio(subPagesLayout, 1);
        setComponentAlignment(subPagesLayout, Alignment.MIDDLE_CENTER);

    }

    @Override
    public void attach() {
        super.attach();

        subscription = navigator.viewChanges().subscribe(v -> {
            navButtons.forEach((name, button) -> {
                if (navigator.isActive(name)) {
                    button.addStyleName("active");
                } else {
                    button.removeStyleName("active");
                }
            });
        });
    }

    @Override
    public void detach() {
        super.detach();

        subscription.dispose();
    }
}
