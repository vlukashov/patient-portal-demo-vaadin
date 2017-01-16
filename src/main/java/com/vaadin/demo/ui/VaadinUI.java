package com.vaadin.demo.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;

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

    LayoutMode current;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        tabsheet = new TabSheet();
        tabsheet.addStyleName("main");
        tabsheet.setSizeFull();
        tabsheet.addComponents(patientView);
        tabsheet.addComponent(analyticsView);

        tabsheet.addSelectedTabChangeListener(e -> closeAllSubViews());

        layout.addComponent(tabsheet);
        setContent(layout);

        current = getLayoutMode();

        Page.getCurrent().addBrowserWindowResizeListener(resize -> {
            LayoutMode layoutMode = getLayoutMode();
            if (!layoutMode.equals(current)) {
                ((MainView) tabsheet.getSelectedTab()).repaint();
                subviews.forEach(subView -> {
                    updateWidths(subView);
                    subView.repaint();
                });
                current = layoutMode;
            }
        });
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
        updateWidths(subView);
        layout.addComponent(subView, "top:0;right:0;z-index:5;"); // magic number 5 ;-)
    }

    private void updateWidths(SubView subView) {
        if (getLayoutMode() == LayoutMode.HANDHELD) {
            // Note!! There is a bug when opening the subView in mobile 100% and moving to desktop version
            // that makes AbstractLayout leave a left: 0; into the css and the sub window is thus in the wrong position.
            subView.setWidth("100%");
        } else {
            subView.setWidth("960px");
        }
    }

    public void closeSubView(SubView subView) {
        closeSubView(subView, true);
    }

    public void closeSubView(SubView subView, boolean informMainPanel) {
        if (subviews.contains(subView)) {
            // close also sub-sub views
            while (subviews.contains(subView)) {
                layout.removeComponent(subviews.removeFirst());
            }
            if (!subviews.isEmpty()) {
                makeSubViewVisible(subviews.getFirst());
            }
            if (informMainPanel)
                ((MainView) tabsheet.getSelectedTab()).subViewClose();
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
