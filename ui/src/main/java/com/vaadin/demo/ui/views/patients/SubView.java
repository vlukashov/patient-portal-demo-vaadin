package com.vaadin.demo.ui.views.patients;


import com.vaadin.ui.Component;

import java.util.Map;

public interface SubView extends Component {

    String getTitle();

    boolean isFullScreen();

    void enter(Map<String, String> params);
}