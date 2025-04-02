package org.classysheet.core.api.domain;

import java.util.List;

@Workbook
public record TestSimpleWorkbook(
        List<TestSimpleRow> rows) {
}
