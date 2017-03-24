package com.vaadin.demo.ui.util;


import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.UI;

public class Nav {

    public static void addListener(ViewChangeListener listener) {
        UI.getCurrent().getNavigator().addViewChangeListener(listener);
    }

    public static void navigateTo(String url) {
        UI.getCurrent().getNavigator().navigateTo(url);
    }

    public static String getState() {
        return UI.getCurrent().getNavigator().getState();
    }

}
