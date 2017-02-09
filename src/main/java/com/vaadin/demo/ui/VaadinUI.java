package com.vaadin.demo.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.*;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;

@SpringUI
@Theme("portal")
public class VaadinUI extends UI {


    @Autowired
    private PatientView patientView;

    @Autowired
    private AnalyticsView analyticsView;

    private TabSheet tabsheet;
    private AbsoluteLayout layout = new AbsoluteLayout();

    private LinkedList<SubView> subviews = new LinkedList();

    private LayoutMode lastRenderMode;
    private Button logout;

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

        lastRenderMode = getLayoutMode();

        logout = new Button("Logout", clickEvent -> {
           Page.getCurrent().setLocation("/logout");
        });
        logout.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        logout.addStyleName("logout-button");
        logout.setIcon(FontAwesome.SIGN_OUT);
        layout.addComponent(logout, "top:0;right:0");

        // Add a resize listener so we can check if we need to redraw in a new mode
        Page.getCurrent().addBrowserWindowResizeListener(resize -> {
            LayoutMode layoutMode = getLayoutMode();
            if (!layoutMode.equals(lastRenderMode)) {
                ((MainView) tabsheet.getSelectedTab()).repaint();
                subviews.forEach(subView -> {
                    updateWidths(subView);
                    subView.repaint();
                });
                lastRenderMode = layoutMode;
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


    /**
     * Close Sub-View and inform main panel that sub view has been closed
     *
     * @param subView SubView to close
     */
    public void closeSubView(SubView subView) {
        closeSubView(subView, true);
    }

    /**
     * Close Sub-View and potentially inform main view of closing.
     * Not informing of closing is usually for closing a sub-sub-view
     *
     * @param subView         SubView to close
     * @param informMainPanel Inform main view of closing.
     */
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
