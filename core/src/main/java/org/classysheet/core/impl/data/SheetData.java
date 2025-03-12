package org.classysheet.core.impl.data;

import org.classysheet.core.impl.meta.SheetMeta;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class SheetData {

    private final SheetMeta sheetMeta;
    private final List<?> rows;

    public static SheetData ofRowDatas(SheetMeta sheetMeta, List<?> rows) {
        return new SheetData(sheetMeta, rows);
    }

    public SheetData(SheetMeta sheetMeta, List<?> rows) {
        this.sheetMeta = sheetMeta;
        this.rows = rows;
        if (rows.isEmpty()) {
            // TODO better error message that says which class etc
            throw new IllegalArgumentException("Rows cannot be empty.");
        }
    }

    public Stream<RowData> streamRowDatas() {
        AtomicInteger index = new AtomicInteger(0);
        return rows.stream().map(row -> new RowData(sheetMeta, index.getAndIncrement(), row));
    }

    // ************************************************************************
    // Getters
    // ************************************************************************

    public SheetMeta sheetMeta() {
        return sheetMeta;
    }

    public List<?> rows() {
        return rows;
    }

}
