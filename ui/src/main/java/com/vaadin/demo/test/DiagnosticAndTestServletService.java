package com.vaadin.demo.test;

import com.vaadin.server.*;
import com.vaadin.spring.server.SpringVaadinServletService;

/**
 * Created by alump on 05/04/2017.
 */
public class DiagnosticAndTestServletService extends SpringVaadinServletService {

    public DiagnosticAndTestServletService(VaadinServlet servlet, DeploymentConfiguration conf, String serviceUrl)
            throws ServiceException {
        super(servlet, conf, serviceUrl);
    }

    @Override
    protected VaadinSession createVaadinSession(VaadinRequest request)
            throws ServiceException {
        return new DiagnosticAndTestServletSession(this);
    }
}
