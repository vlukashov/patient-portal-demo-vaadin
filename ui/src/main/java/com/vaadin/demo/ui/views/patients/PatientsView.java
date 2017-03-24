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
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@SpringComponent
@SpringView(name = "patients")
public class PatientsView extends VerticalLayout implements View {

    @Autowired
    PatientRepository repo;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("Patients");
        setSizeFull();
        buildLayout();
    }

    private void buildLayout() {
        Grid<Patient> patientsGrid = new Grid<>(Patient.class);
        patientsGrid.setSizeFull();

        patientsGrid.removeAllColumns();
        patientsGrid.addColumn(patient -> patient.getFirstName() + " " + patient.getLastName()).setId("name").setCaption("Name");
        patientsGrid.addColumn(patient -> patient.getId().toString()).setId("id").setCaption("Id");
        patientsGrid.addColumn(patient -> patient.getMedicalRecord().toString()).setId("medicalRecord").setCaption("Medical record");
        patientsGrid.addColumn(patient -> patient.getDoctor().getFirstName() + " " + patient.getDoctor().getLastName()).setId("doctor").setCaption("Doctor");
        patientsGrid.addColumn(patient ->
                patient.getLastVisit() == null ? "" : SimpleDateFormat.getDateInstance().format(patient.getLastVisit())
        ).setId("lastVisit").setCaption("Last visit");

//        patientsGrid.setItems(repo.findAll());
        patientsGrid.setDataProvider(
                (sortOrder, offset, limit) -> {
                    int page = offset / limit;
                    Sort sorts = getSorts(sortOrder);
                    List<Patient> content = repo.findAll(new PageRequest(page, limit, sorts)).getContent();
                    return content.stream();
                },
                () -> (int) repo.count());

        addComponent(patientsGrid);
    }

    private Sort getSorts(List<QuerySortOrder> sortOrders) {
        if(sortOrders.isEmpty()){
            return new Sort(new Sort.Order(Sort.Direction.DESC, "lastName"));
        } else {
            return new Sort(sortOrders.stream().map(sortOrder -> {
                Sort.Direction dir = sortOrder.getDirection().equals(SortDirection.ASCENDING) ? Sort.Direction.ASC : Sort.Direction.DESC;
                return new Sort.Order(dir, sortOrder.getSorted());
            }).collect(Collectors.toList()));
        }
    }
}
