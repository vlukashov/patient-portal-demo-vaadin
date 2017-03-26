package com.vaadin.demo.ui.views.analytics;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.demo.service.AnalyticsService;
import com.vaadin.demo.service.StringLongPair;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;

@SpringComponent
@SpringView
public class AnalyticsView extends VerticalLayout implements View {

    @Autowired
    private AnalyticsService service;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("Analytics");
    }

    @PostConstruct
    void init() {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
        tabSheet.addStyleName("detail-tabs");
        addComponent(tabSheet);

        tabSheet.addTab(getAgeChart(), "Age");
        tabSheet.addTab(getGenderChart(), "Gender");
        tabSheet.addTab(getDoctorChart(), "Doctor");

        addComponentsAndExpand(tabSheet);
    }

    private Layout getAgeChart() {
        List<StringLongPair> data = service.getStatsByAge();
        data.sort(Comparator.comparing(StringLongPair::getGroup));

        return getChart("Patients bt Age", data);
    }

    private Layout getGenderChart() {
        return getChart("Gender", service.getStatsByGender());
    }

    private Layout getDoctorChart() {

        return getChart("Avg. Patient age by Doctor", service.getStatsByDoctor());
    }

    private Layout getChart(String title,  List<StringLongPair> data) {
        Chart chart = new Chart(ChartType.COLUMN);
        chart.getConfiguration().setTitle(title);
        DataSeries ds = new DataSeries();
        data.forEach(d -> ds.add(new DataSeriesItem(d.getGroup(), d.getCount())));
        chart.getConfiguration().addSeries(ds);
        chart.getConfiguration().getxAxis().setType(AxisType.CATEGORY);

        return new VerticalLayout(chart);
    }
}
