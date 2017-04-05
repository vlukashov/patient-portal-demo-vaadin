package com.vaadin.demo;

import com.vaadin.demo.test.DiagnosticAndTestServletService;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServletService;
import com.vaadin.spring.server.SpringVaadinServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Extends servlet to allow enabling test overrides
 */
@Component("vaadinServlet")
public class PatientPortalServlet extends SpringVaadinServlet {

    @Value("${vaadin.gatling.mode}")
    private String loadTestMode;

    private boolean isTestMode() {
        try {
            return Boolean.parseBoolean(loadTestMode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected VaadinServletService createServletService(DeploymentConfiguration conf) throws ServiceException {
        if(!isTestMode()) {
            System.out.println("Test mode not enabled");
            return super.createServletService(conf);
        } else {
            System.out.println("Test mode enabled");
            DiagnosticAndTestServletService service = new DiagnosticAndTestServletService(this,
                    conf, this.getServiceUrlPath());
            service.init();
            return service;
        }
    }
}
