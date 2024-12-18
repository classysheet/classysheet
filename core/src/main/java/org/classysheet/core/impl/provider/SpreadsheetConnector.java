package org.classysheet.core.impl.provider;

import org.classysheet.core.impl.data.WorkbookData;

public interface SpreadsheetConnector {

    void writeWorkbook(WorkbookData workbookData);

}
