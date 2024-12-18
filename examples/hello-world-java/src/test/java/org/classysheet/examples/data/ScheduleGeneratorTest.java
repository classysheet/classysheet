package org.classysheet.examples.data;

import org.classysheet.core.api.ClassysheetService;
import org.classysheet.examples.domain.Schedule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ScheduleGeneratorTest {

    @Test
    public void generateDemoData() {
        assertThat(ScheduleGenerator.generateDemoData()).isNotNull();
    }

}