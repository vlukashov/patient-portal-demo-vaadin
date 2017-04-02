package com.vaadin.demo.ui.views.patients;

import com.vaadin.demo.ui.service.PatientsService;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import io.reactivex.subjects.PublishSubject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringComponent
@ViewScope
public class SubViewNavigator {
    private Map<String, SubView> viewMap = new HashMap<>();
    private PublishSubject<SubView> viewSubject = PublishSubject.create();

    private String prefix;
    private String fallback;
    private String activePath;
    private Map<String, String> activeParams = new HashMap<>();
    private PatientsService patientsService;

    @Autowired
    public SubViewNavigator(PatientsService patientsService) {
        this.patientsService = patientsService;

        patientsService.getCurrentPatient().subscribe(patient -> {
            patient.ifPresent(p -> {
                Long id = p.getId();
                if(id != null && !id.toString().equals(activeParams.get("id"))) {
                    activeParams.put("id", id.toString());
                    navigateToPath(activePath);
                } else {
                    activeParams.remove("id");
                    navigateToPath("new");
                }
            });
        });

    }

    public void setFallback(String fallback) {
        this.fallback = fallback;
    }

    public void addView(String pattern, SubView view) {
        viewMap.put(pattern, view);
    }

    /**
     * Navigate to a pattern and fill in the blanks from the current state params.
     *
     * @param pattern
     */
    public void navigateToPath(String pattern) {
        SubView view = viewMap.get(pattern);
        view.enter(activeParams);
        activePath = pattern;
        viewSubject.onNext(view);

        String url = pattern;
        for (Map.Entry<String, String> params : activeParams.entrySet()) {
            url = pattern.replaceAll(":" + params.getKey(), params.getValue());
        }
        updateUriFragment(url);
    }

    /**
     * Navigate to a url like '12/profile/edit' and populate current state based on params.
     *
     * @param path
     */
    public void navigateByUrl(String path) {
        // find view based on pattern
        viewMap.keySet().stream().filter(pattern -> path.matches(getRegex(pattern))).findFirst().ifPresent(pattern -> {
            activePath = pattern;

            // extract view params
            String regex = getRegex(pattern);
            Matcher matcher = Pattern.compile(regex).matcher(path);

            activeParams = new HashMap<>();

            if (matcher.find()) {
                // How the fuck is there not a way of getting the collection of matched groups from Matcher?
                Matcher lol = Pattern.compile(":(\\w+)").matcher(pattern);
                while (lol.find()) {
                    String group = lol.group().substring(1);
                    activeParams.put(group, matcher.group(group));
                }
            }

            // call enter on view with params, show view
            SubView view = viewMap.get(pattern);
            view.enter(activeParams);
            viewSubject.onNext(view);

            // update path
            updateUriFragment(path);
        });
    }

    /**
     * Turn simplified pattern into regular expression
     *
     * @param pattern
     * @return
     */
    private String getRegex(String pattern) {
        String regex = pattern.replaceAll(":(\\w+)", "(?<$1>\\\\w+)");
        regex = "^" + regex + "$";
        return regex;
    }

    private void updateUriFragment(String url) {
        String fragment = prefix + "/" + url;
        System.out.println("Setting URI Fragment: " + fragment);
        Page.getCurrent().setUriFragment(fragment, false);
    }

    public boolean isActive(String viewName) {
        return activePath != null && viewName.equals(activePath);
    }

    public PublishSubject<SubView> viewChanges() {
        return viewSubject;
    }

    /**
     * Initialize navigation state from current page URI fragment
     *
     * @param prefix
     * @param parameters
     */
    public void init(String prefix, String parameters) {

        this.prefix = prefix;

        if (!parameters.isEmpty()) {
            navigateByUrl(parameters);
        } else {
            activePath = fallback;
        }
    }

    public void close() {
        patientsService.getCurrentPatient().onNext(Optional.empty());
        Page.getCurrent().setUriFragment(prefix, false);
    }
}
