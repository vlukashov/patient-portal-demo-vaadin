package com.vaadin.demo.test;

import com.vaadin.server.*;
import com.vaadin.ui.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Extends VaadinSession to help with connector IDs
 */
public class DiagnosticAndTestServletSession extends VaadinSession {

    private Map<Class<? extends ClientConnector>, AtomicInteger> squences = new HashMap<>();

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
            connectorId = getGenericConnectorId(connector);
        }

        return connectorId;
    }

    private String getGenericConnectorId(ClientConnector connector) {
        return "#" + connector.getClass().getSimpleName().toLowerCase() + "-" + nextId(connector.getClass());
    }

    private int nextId(Class<? extends ClientConnector> c) {
        AtomicInteger id = squences.get(c);
        if(id == null) {
            id = new AtomicInteger(0);
            squences.put(c, id);
        }

        return id.incrementAndGet();
    }
}
