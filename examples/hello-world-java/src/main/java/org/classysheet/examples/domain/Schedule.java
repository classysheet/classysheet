package org.classysheet.examples.domain;

import org.classysheet.core.api.domain.Workbook;

import java.util.List;

@Workbook
public class Schedule {

    private List<Employee> employees;
    private List<Shift> shifts;

    public Schedule(List<Employee> employees, List<Shift> shifts) {
        this.employees = employees;
        this.shifts = shifts;
    }


    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Shift> getShifts() {
        return shifts;
    }

    public void setShifts(List<Shift> shifts) {
        this.shifts = shifts;
    }
}
