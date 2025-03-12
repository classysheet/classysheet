package org.classysheet.examples.quarkus.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.classysheet.core.api.ClassysheetService;
import org.classysheet.examples.quarkus.data.ScheduleGenerator;
import org.classysheet.examples.quarkus.domain.Schedule;

import java.io.IOException;

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

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("read-excel")
    public void readExcel(MultipartFormDataInput input) {
        if (input.getParts().size() != 1) {
            throw new IllegalArgumentException("Expected exactly one file, but got " + input.getParts().size() + " files.");
        }
        InputPart inputPart = input.getParts().get(0);
        try {
            schedule = classysheetService.readExcelInputStream(inputPart.getBody());
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read input file.", e);
        }
    }

}
