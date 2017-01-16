package com.vaadin.demo.ui;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by mstahv
 */
public abstract class SubView extends AbsoluteLayout {

    private final TabSheet tabSheet = new TabSheet();
    private Component topRightComponent;

    public SubView() {
        setSizeFull();
        tabSheet.setSizeFull();
        tabSheet.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
        tabSheet.addStyleName("detail-tabs");
        CssLayout cssLayout = new CssLayout(tabSheet);
        cssLayout.setStyleName(ValoTheme.LAYOUT_CARD);
        cssLayout.setSizeFull();
        addComponent(cssLayout);
    }

    public void addTab(Component c) {
        if (c.getCaption() == null) {
            c.setCaption(getClass().getSimpleName());
        }
        tabSheet.addComponent(c);
    }

    protected TabSheet getTabsheet() {
        return tabSheet;
    }

    public void setTopRightComponent(Component c) {
        if(topRightComponent != null) {
            removeComponent(topRightComponent);
        }
        addComponent(c, "top:0;right:0");
        topRightComponent = c;
    }

    public void setTopLeftComponent(Component c) {
        addComponent(c, "top:0;left:0");
    }

    public void setToolbar(Component c) {
        addComponent(c, "bottom:0;left:0right:0;");
        ComponentPosition componentPosition = new ComponentPosition();
        componentPosition.setCSSString("top:0;bottom:50px;right:0;left:0;");
        setPosition(tabSheet, componentPosition);
    }

    /**
     * Shows this sub view.
     */
    public void show() {
        ((VaadinUI) UI.getCurrent()).showSubView(this);
    }

    /**
     * Shows this sub view.
     */
    public void showAndCloseExisting() {
        ((VaadinUI) UI.getCurrent()).closeAllSubViews();
        ((VaadinUI) UI.getCurrent()).showSubView(this);
    }

    /**
     * Closes this subview.
     */
    public void close() {
        ((VaadinUI) getUI()).closeSubView(this);
    }

    public abstract void repaint();
}
