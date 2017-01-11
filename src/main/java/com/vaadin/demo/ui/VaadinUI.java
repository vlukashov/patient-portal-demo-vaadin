package com.vaadin.demo.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;

import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;

@SpringUI
@Theme("portal")
public class VaadinUI extends UI {

    private TabSheet tabsheet;

    @Autowired
    PatientView patientView;

    @Autowired
    AnalyticsView analyticsView;

    AbsoluteLayout layout = new AbsoluteLayout();

    private LinkedList<SubView> subviews = new LinkedList();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        tabsheet = new TabSheet();
        tabsheet.addStyleName("main");
        tabsheet.setSizeFull();
        tabsheet.addComponents(patientView);
        tabsheet.addComponent(analyticsView);

        tabsheet.addSelectedTabChangeListener(e->closeAllSubViews());

        layout.addComponent(tabsheet);
        setContent(layout);
    }

    public void showSubView(SubView subView) {
        if (!subviews.isEmpty()) {
            if (subviews.getFirst() == subView) {
                return;
            }
            layout.removeComponent(subviews.getFirst());
        }
        subviews.addFirst(subView);
        makeSubViewVisible(subView);
    }

    private void makeSubViewVisible(SubView subView) {
        if(getLayoutMode() == LayoutMode.HANDHELD) {
            subView.setWidth("100%");
        } else {
            subView.setWidth("960px");
        }
        layout.addComponent(subView, "top:0;bottom:0;right:0;z-index:5;"); // magic number 5 ;-)
    }

    public void closeSubView(SubView subView) {
        if (subviews.contains(subView)) {
            // close also sub-sub views
            while (subviews.contains(subView)) {
                layout.removeComponent(subviews.removeFirst());
            }
            if (!subviews.isEmpty()) {
                makeSubViewVisible(subviews.getFirst());
            }
            ((MainView)tabsheet.getSelectedTab()).subViewClose();
        }
    }

    public void closeAllSubViews() {
        while (!subviews.isEmpty()) {
            closeSubView(subviews.getFirst());
        }
    }

    public LayoutMode getLayoutMode() {
        int width = Page.getCurrent().getBrowserWindowWidth();
        if (width < 1024) {
            return LayoutMode.HANDHELD;
        } else {
            return LayoutMode.DESKTOP;
        }
    }
}
