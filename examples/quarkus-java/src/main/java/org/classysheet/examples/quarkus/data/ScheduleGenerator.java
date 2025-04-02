package org.classysheet.examples.quarkus.data;

import org.classysheet.examples.quarkus.domain.Employee;
import org.classysheet.examples.quarkus.domain.Gender;
import org.classysheet.examples.quarkus.domain.Schedule;
import org.classysheet.examples.quarkus.domain.Shift;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ScheduleGenerator {

    public static Schedule generateDemoData() {
        Employee ann = new Employee("Ann", Gender.FEMALE, 30, 123.45);
        Employee beth = new Employee("Beth", Gender.FEMALE, 50, 345.67);
        Employee carl = new Employee("Carl", Gender.MALE, 40, 234.56);
        List<Employee> employees = List.of(ann, beth, carl);
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        List<Shift> shifts = List.of(
               new Shift("1", LocalDateTime.of(today, LocalTime.of(6, 0)), LocalDateTime.of(today, LocalTime.of(14, 0)), ann),
               new Shift("2", LocalDateTime.of(today, LocalTime.of(14, 0)), LocalDateTime.of(today, LocalTime.of(22, 0)), beth),
               new Shift("3", LocalDateTime.of(tomorrow, LocalTime.of(6, 0)), LocalDateTime.of(tomorrow, LocalTime.of(14, 0)), ann),
               new Shift("4", LocalDateTime.of(tomorrow, LocalTime.of(14, 0)), LocalDateTime.of(tomorrow, LocalTime.of(22, 0)), null)
        );
        return new Schedule(employees, shifts);
    }

}
