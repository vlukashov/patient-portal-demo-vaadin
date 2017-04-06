package com.vaadin.demo;

import com.vaadin.demo.test.DiagnosticAndTestServletService;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServletService;
import com.vaadin.spring.server.SpringVaadinServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    public void init(ServletConfig config) throws ServletException {
        if(isTestMode()) {
            System.setProperty("vaadin.server.disable-xsrf-protection", "true");
            System.setProperty("vaadin.disable-xsrf-protection", "true");
            System.setProperty("disable-xsrf-protection", "true");
            System.setProperty("com.vaadin.spring.server.disable-xsrf-protection", "true");
        }

        super.init(config);
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

    @Override
    protected void service(HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException {

        if(isTestMode()) {
            System.out.println("Request: " + request.getRequestURI());
        }

        super.service(request, response);
    }
}
