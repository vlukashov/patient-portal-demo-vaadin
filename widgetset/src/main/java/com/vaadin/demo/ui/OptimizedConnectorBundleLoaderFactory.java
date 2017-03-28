package com.vaadin.demo.ui;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.vaadin.client.ui.ui.UIConnector;
import com.vaadin.server.widgetsetutils.ConnectorBundleLoaderFactory;
import com.vaadin.shared.ui.Connect.LoadStyle;

public class OptimizedConnectorBundleLoaderFactory extends
        ConnectorBundleLoaderFactory {
    private Set<String> eagerConnectors = new HashSet<String>();

    {
        eagerConnectors.add(com.vaadin.client.ui.ui.UIConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.ui.formlayout.FormLayoutConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.ui.textfield.TextFieldConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.ui.label.LabelConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.ui.passwordfield.PasswordFieldConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.ui.nativebutton.NativeButtonConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.ui.orderedlayout.VerticalLayoutConnector.class.getName());
    }

    private Set<String> deferredConnectors = new HashSet<String>();
    {
        eagerConnectors.add(com.vaadin.client.connectors.grid.DetailsManagerConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.connectors.grid.TextRendererConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.connectors.grid.ColumnConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.connectors.grid.GridConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.connectors.grid.EditorConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.ui.orderedlayout.HorizontalLayoutConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.connectors.grid.SingleSelectionModelConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.ui.csslayout.CssLayoutConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.connectors.data.DataCommunicatorConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.ui.tabsheet.TabsheetConnector.class.getName());
        eagerConnectors.add(com.vaadin.addon.charts.shared.ChartConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.ui.image.ImageConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.ui.formlayout.FormLayoutConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.ui.datefield.PopupDateFieldConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.ui.combobox.ComboBoxConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.ui.textarea.TextAreaConnector.class.getName());
        eagerConnectors.add(com.vaadin.client.ui.datefield.DateFieldConnector.class.getName());
    }

    @Override
    protected LoadStyle getLoadStyle(JClassType connectorType) {
        if (eagerConnectors.contains(connectorType.getQualifiedBinaryName())) {
            return LoadStyle.EAGER;
        } else if (deferredConnectors.contains(connectorType.getQualifiedBinaryName())) {
            return LoadStyle.DEFERRED;
        } else {
            return LoadStyle.LAZY;
        }
    }
}
