package org.classysheet.examples;

import org.classysheet.core.api.ClassysheetService;
import org.classysheet.examples.domain.Schedule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExcelExampleTest {

    @Test
    public void classysheetService() {
        assertThat(ClassysheetService.create(Schedule.class)).isNotNull();
    }

}