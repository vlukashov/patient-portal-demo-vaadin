package com.vaadin.demo.ui;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by mstahv
 */
public abstract class MainView extends VerticalLayout implements View {

    public MainView() {
        setCaption(getClass().getSimpleName().replaceAll("View",""));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        // NOP, not needed
    }

}
