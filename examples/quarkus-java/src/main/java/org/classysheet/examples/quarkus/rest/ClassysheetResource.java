package org.classysheet.examples.quarkus.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.classysheet.core.api.ClassysheetService;
import org.classysheet.examples.quarkus.data.ScheduleGenerator;
import org.classysheet.examples.quarkus.domain.Schedule;

@Path("classysheet")
public class ClassysheetResource {

    private ClassysheetService<Schedule> classysheetService = ClassysheetService.create(Schedule.class);

    private Schedule schedule = ScheduleGenerator.generateDemoData();

    @GET
    @Path("demo-data")
    public Schedule demoData() {
        return schedule;
    }

    @POST
    @Path("write-excel")
    public void writeExcel() {
        classysheetService.writeExcelTmpFileAndShow(schedule);
    }

}
