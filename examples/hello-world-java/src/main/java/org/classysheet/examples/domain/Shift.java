package org.classysheet.examples.domain;

import org.classysheet.core.api.domain.Sheet;

import java.time.LocalDateTime;

@Sheet
public class Shift {

    private String id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Employee employee;

    public Shift(String id, LocalDateTime start, LocalDateTime end, Employee employee) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.employee = employee;
    }

    @Override
    public String toString() {
        return "Shift[id=" + id + ", start=" + start +", end=" + end
                + ", employee=" + (employee == null ? "null" : employee.name()) + "]";
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

}
