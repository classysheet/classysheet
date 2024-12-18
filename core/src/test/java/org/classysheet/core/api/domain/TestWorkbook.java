package org.classysheet.core.api.domain;

import java.util.List;

@Workbook
public record TestWorkbook(
        List<TestSheet> testSheets,
        List<TestSheetAlt> testSheetAlts) {}
