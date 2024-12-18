package org.classysheet.core.impl.provider.google;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

@Disabled() // TODO Enable test when it works on any machine (without sharing secrets)
public class GoogleSheetTest {
    private static Sheets sheetsService;
    private static String SPREADSHEET_ID = "19sd5Bw51OsHruzZzkpygXkatBwjSkSxZPZpiW_hzoV8";

    @BeforeAll
    public static void setup() throws GeneralSecurityException, IOException {
        sheetsService = SheetsServiceUtil.getSheetsService();
    }
    @Test
    public void whenWriteSheet_thenReadSheetOk() throws IOException {
        // adds these row by row
        ValueRange body = new ValueRange()
                .setValues(Arrays.asList(
                        Arrays.asList("Expenses January"), // row 1
                        Arrays.asList("books", "30"), // row 2
                        Arrays.asList("pens", "10"), // row 3
                        Arrays.asList("Expenses February"), // row 4
                        Arrays.asList("clothes", "20"), // row 5
                        Arrays.asList("shoes", "5"))); // row 6
        UpdateValuesResponse result = sheetsService.spreadsheets().values()
                .update(SPREADSHEET_ID, "A1", body)
                .setValueInputOption("RAW")
                .execute();
    }
}
