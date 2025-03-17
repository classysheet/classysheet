package org.classysheet.core.api.domain;

import java.util.List;

@Workbook
public record TestWorkbook(
        List<TestRow> rows,
        List<TestSimpleRow> simpleRows,
        @SheetIgnore String status) {

    public TestWorkbook(List<TestRow> rows, List<TestSimpleRow> simpleRows) {
        this(rows, simpleRows, null);
    }

}
