package org.classysheet.core.api;

import org.classysheet.core.api.domain.Workbook;

import java.util.List;

@Workbook
public record TestWorkbook(
        List<TestSheet> testSheets,
        List<TestSheetAlt> testSheetAlts) {}
