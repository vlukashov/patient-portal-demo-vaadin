package com.vaadin.demo.test;

import com.vaadin.server.*;
import com.vaadin.ui.Component;
;

import java.util.HashMap;
import java.util.Map;

/**
 * Extends VaadinSession to help with connector IDs
 */
public class DiagnosticAndTestServletSession extends VaadinSession {

    private Map<Class<? extends ClientConnector>, Integer> sequences = new HashMap<>();

    public DiagnosticAndTestServletSession(VaadinService service) {
        super(service);
    }

    @SuppressWarnings("deprecation")
    @Override
    public String createConnectorId(ClientConnector connector) {
        String connectorId = "";
        if (connector instanceof Component) {
            Component component = (Component) connector;
            connectorId = component.getId() == null ? super
                    .createConnectorId(connector) : component.getId();
        } else {
            connectorId = super.createConnectorId(connector);
        }

        return connectorId;
    }

    private int nextId(Class<? extends ClientConnector> c) {
        Integer nextid = 0;
        if (sequences.get(c)!=null) {
            nextid = sequences.get(c);
        }
        sequences.put(c, nextid+1);
        return nextid;
    }
}
