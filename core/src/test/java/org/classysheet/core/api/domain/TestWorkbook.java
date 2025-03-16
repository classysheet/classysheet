package org.classysheet.core.api.domain;

import java.util.List;

@Workbook
public record TestWorkbook(
        List<TestSheet> testSheets,
        List<TestSheetAlt> testSheetAlts,
        @SheetIgnore String status) {

    public TestWorkbook(List<TestSheet> testSheets, List<TestSheetAlt> testSheetAlts) {
        this(testSheets, testSheetAlts, null);
    }

}
