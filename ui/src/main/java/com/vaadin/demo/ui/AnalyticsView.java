package com.vaadin.demo.ui;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.demo.service.AnalyticsService;
import com.vaadin.demo.service.StringLongPair;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;


@SpringComponent
@UIScope
public class AnalyticsView extends MainView {

    private final AnalyticsService service;

    public AnalyticsView(AnalyticsService service) {
        this.service = service;
    }

    @PostConstruct
    void init() {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.setStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
        tabSheet.addStyleName("detail-tabs");
        addComponent(tabSheet);

        tabSheet.addTab(getAgeChart(), "Age");
        tabSheet.addTab(getGenderChart(), "Gender");
        tabSheet.addTab(getDoctorChart(), "Doctor");

        addComponent(tabSheet);
        setExpandRatio(tabSheet, 1);
    }

    private Layout getAgeChart() {
        Chart chart = new Chart(ChartType.COLUMN);
        chart.getConfiguration().setTitle("Patients bt Age");
        DataSeries ds = new DataSeries();
        List<StringLongPair> data = service.getStatsByAge();

        data.sort(Comparator.comparing(StringLongPair::getGroup));

        data.forEach(d->ds.add(new DataSeriesItem(d.getGroup(),d.getCount())));
        chart.getConfiguration().addSeries(ds);
        chart.getConfiguration().getxAxis().setType(AxisType.CATEGORY);

        VerticalLayout vl = new VerticalLayout(chart);
        vl.setMargin(true);
        return vl;
    }

    private Layout getGenderChart() {
        Chart chart = new Chart(ChartType.COLUMN);
        chart.getConfiguration().setTitle("Gender");
        DataSeries ds = new DataSeries();

        List<StringLongPair> data = service.getStatsByGender();
        data.forEach(d->ds.add(new DataSeriesItem(d.getGroup(),d.getCount())));
        chart.getConfiguration().addSeries(ds);
        chart.getConfiguration().getxAxis().setType(AxisType.CATEGORY);

        VerticalLayout vl = new VerticalLayout(chart);
        vl.setMargin(true);
        return vl;
    }

    private Layout getDoctorChart() {
        Chart chart = new Chart(ChartType.COLUMN);
        chart.getConfiguration().setTitle("Avg. Patient age by Doctor");
        DataSeries ds = new DataSeries();

        List<StringLongPair> data = service.getStatsByDoctor();
        data.forEach(d->ds.add(new DataSeriesItem(d.getGroup(),d.getCount())));
        chart.getConfiguration().addSeries(ds);
        chart.getConfiguration().getxAxis().setType(AxisType.CATEGORY);

        VerticalLayout vl = new VerticalLayout(chart);
        vl.setMargin(true);
        return vl;
    }

    @Override
    public void subViewClose() {
        // NOOP
    }

    @Override
    public void repaint() {
        // NOOP
    }
}
