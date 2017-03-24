package com.vaadin.demo.ui.views.patients;

import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.demo.entities.Patient;
import com.vaadin.demo.repositories.PatientRepository;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@SpringComponent
@SpringView(name = "patients")
public class PatientsView extends CssLayout implements View {

    @Autowired
    private PatientRepository repo;

    @Autowired
    private PatientDetailsView detailsView;

    @Autowired
    private PatientSubject patientSubject;

    private Grid<Patient> patientsGrid;

    public PatientsView() {
        addStyleName("patients-view");
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("Patients");
        setSizeFull();
        buildLayout();

        detailsView.init();
        addComponent(detailsView);

        patientSubject.get().subscribe(p -> p.ifPresent(patient -> patientsGrid.select(patient)));
    }

    private void buildLayout() {
        patientsGrid = new Grid<>(Patient.class);
        patientsGrid.setSizeFull();
        patientsGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        patientsGrid.removeAllColumns();
        patientsGrid.addColumn(patient -> patient.getLastName() + ", " + patient.getFirstName()).setId("lastName").setCaption("Name");
        patientsGrid.addColumn(patient -> patient.getId().toString()).setId("id").setCaption("Id");
        patientsGrid.addColumn(patient -> patient.getMedicalRecord().toString()).setId("medicalRecord").setCaption("Medical record");
        patientsGrid.addColumn(patient -> patient.getDoctor().getLastName() + ", " + patient.getDoctor().getFirstName()).setId("doctor.lastName").setCaption("Doctor");
        patientsGrid.addColumn(patient ->
                patient.getLastVisit() == null ? "" : SimpleDateFormat.getDateInstance().format(patient.getLastVisit())
        ).setId("lastVisit").setCaption("Last visit");

        patientsGrid.setItems(repo.findAll());

//        Vaadin Grid Data Provider API is a PITA
//        patientsGrid.setDataProvider((sortOrder, offset, limit) -> {
//            System.out.println("Getting: offset: " + offset + ", limit: " + limit + " (page: " + (offset / limit) + ")");
//            List<Patient> content = repo.findAll(new PageRequest(offset / limit, limit, getSorts(sortOrder))).getContent();
//            return content.stream();
//        }, () -> (int) repo.count());

        patientsGrid.addSelectionListener(e -> {
            patientSubject.get().onNext(e.getFirstSelectedItem());
        });

        addComponent(patientsGrid);
    }


    private Sort getSorts(List<QuerySortOrder> sortOrders) {
        if (sortOrders.isEmpty()) {
            return new Sort(new Sort.Order(Sort.Direction.DESC, "lastName"));
        } else {
            return new Sort(sortOrders.stream().map(sortOrder -> {
                Sort.Direction dir = sortOrder.getDirection().equals(SortDirection.ASCENDING) ? Sort.Direction.ASC : Sort.Direction.DESC;
                String sortProperty = sortOrder.getSorted();
                return new Sort.Order(dir, sortProperty);
            }).collect(Collectors.toList()));
        }
    }

}
