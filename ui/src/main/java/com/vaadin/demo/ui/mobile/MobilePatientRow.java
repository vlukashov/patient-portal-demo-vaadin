package com.vaadin.demo.ui.mobile;

import com.vaadin.demo.entities.Patient;

/**
 * Created by mstahv
 */
public class MobilePatientRow extends MobileRow {

    private final Patient patient;
    private MobilePatientListing mobilePatientListing;

    public MobilePatientRow(Patient patient, MobilePatientListing mobilePatientListing) {
        super(patient.getLastName() + ", " + patient.getFirstName(), "");
        setSpacing(false);
        this.patient = patient;
        this.mobilePatientListing = mobilePatientListing;
    }

    @Override
    protected void showDetails(boolean expanded) {
        clear();
        if (expanded) {
            showDetail("SSN", patient.getSsn());
            showDetail("Medical record", patient.getMedicalRecord());
            showDetail("Doctor", patient.getDoctor());
            showDetail("Birth date", patient.getBirthDate());
        } else if (getMobileListing().getSortProperty().contains("doctor")) {
            showDetail("Doctor", patient.getDoctor());
        }
    }

    public MobilePatientListing getMobileListing() {
        return mobilePatientListing;
    }
}
