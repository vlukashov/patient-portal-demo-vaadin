package com.vaadin.demo.ui.views.patients;

import com.vaadin.demo.ui.views.patients.journal.JournalListingView;
import com.vaadin.demo.ui.views.patients.profile.ProfileEditView;
import com.vaadin.demo.ui.views.patients.profile.ProfileView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import io.reactivex.disposables.Disposable;

import java.util.LinkedHashMap;
import java.util.Map;

class SubNavBar extends HorizontalLayout {

    private SubViewNavigator navigator;
    private Disposable subscription;
    private Map<String, Button> navButtons = new LinkedHashMap<>();

    SubNavBar(SubViewNavigator navigator) {
        this.navigator = navigator;
        addStyleName("sub-nav-bar");
        setWidth("100%");
        setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        NativeButton backButton = new NativeButton("All patients", click -> navigator.close());
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
