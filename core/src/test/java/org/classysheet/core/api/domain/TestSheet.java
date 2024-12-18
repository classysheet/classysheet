package org.classysheet.core.api.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Sheet
public record TestSheet(
        String stringValue,
        int intValue,
        LocalDate dateValue,
        LocalTime timeValue,
        LocalDateTime dateTimeValue
) {}
