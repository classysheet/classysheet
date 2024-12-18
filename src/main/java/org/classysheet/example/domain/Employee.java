package org.classysheet.example.domain;

import org.classysheet.core.api.domain.IdColumn;
import org.classysheet.core.api.domain.Sheet;

@Sheet
public record Employee(
        @IdColumn String name,
        long age,
        double cost) {
}
