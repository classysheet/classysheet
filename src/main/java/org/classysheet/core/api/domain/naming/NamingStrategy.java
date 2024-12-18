package org.classysheet.core.api.domain.naming;

import org.classysheet.core.api.domain.Column;
import org.classysheet.core.api.domain.Sheet;
import org.classysheet.core.api.domain.Workbook;

import java.lang.reflect.Field;

public interface NamingStrategy {

    /**
     * Only used if {@link Workbook#name()} is null.
     *
     * @param workbookClass never null
     * @return never null
     */
    String workbookName(Class<?> workbookClass);

    /**
     * Only used if {@link Sheet#name()} is null.
     *
     * @param sheetClass never null
     * @return never null
     */
    String sheetName(Class<?> sheetClass);

    /**
     * Only used if {@link Column#name()} is null.
     *
     * @param columnField never null
     * @return never null
     */
    String columnName(Field columnField);

}
