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

    private Map<Class<? extends ClientConnector>, Integer> sequences = new HashMap<>();

    private Map<Class<? extends Extension>, AtomicInteger> extensionSquences = new HashMap<>();

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
        } else if(connector instanceof Extension) {
            Extension extension = (Extension)connector;
            connectorId = "_ext-" + extension.getClass().getSimpleName().toLowerCase() + "-" + nextId(extension.getClass());
        } else {
            connectorId = super.createConnectorId(connector);
        }

        return connectorId;
    }

    private int nextId(Class<? extends Extension> c) {
        AtomicInteger id = extensionSquences.get(c);
        if(id == null) {
            id = new AtomicInteger(0);
            extensionSquences.put(c, id);
        }

        return id.incrementAndGet();
    }
}
