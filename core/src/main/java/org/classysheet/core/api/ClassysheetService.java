package org.classysheet.core.api;

import org.classysheet.core.api.domain.Workbook;
import org.classysheet.core.impl.DefaultClassysheetService;
import org.classysheet.core.impl.meta.WorkbookMeta;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public interface ClassysheetService<Workbook_> {

    /**
     * Create a new service from domain annotations.
     * Typically called once at bootstrap or build time.
     *
     * @param workbookClass never null
     * @param <Workbook_> The class with a {@link Workbook} annotation.
     * @return never null
     */
    static <Workbook_> ClassysheetService<Workbook_> create(
            Class<Workbook_> workbookClass) {
        return new DefaultClassysheetService<>(workbookClass);
    }

    void writeWorkbookToGoogle(Workbook_ workbook);

    Workbook_ readExcelFile(File file);

    Workbook_ readExcelInputStream(InputStream inputStream);

    void writeExcelOutputStream(Workbook_ workbook, OutputStream outputStream);

    File writeExcelTmpFileAndShow(Workbook_ workbook);

}
