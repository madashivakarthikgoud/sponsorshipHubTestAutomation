package dataProviders;

import org.testng.annotations.DataProvider;
import utils.ExcelUtils;

import java.io.IOException;

/**
 * Centralized TestNG DataProvider for all test modules.
 *
 * Excel file location: src/test/resources/TestData.xlsx
 *
 * Sheets consumed:
 *   - SignUp_Data  : columns → testCaseID | userName | email | password | role | expectedResult | expectedMessage
 *   - Login_Data   : columns → testCaseID | email | password | expectedResult | expectedMessage
 */
public class TestDataProviders {

    private static final String EXCEL_PATH =
        System.getProperty("user.dir") + "/src/test/resources/TestData.xlsx";

    // ─── Signup Data ───────────────────────────────────────────────────────────

    /**
     * Provides signup test data from the "SignUp_Data" sheet.
     * Covers UM_TC_01 → UM_TC_08.
     */
    @DataProvider(name = "signUpData")
    public Object[][] getSignUpData() throws IOException {
        return readSheetData("SignUp_Data");
    }

    // ─── Login Data ────────────────────────────────────────────────────────────

    /**
     * Provides login test data from the "Login_Data" sheet.
     * Covers UM_TC_09 → UM_TC_14.
     */
    @DataProvider(name = "loginData")
    public Object[][] getLoginData() throws IOException {
        return readSheetData("Login_Data");
    }

    // ─── Internal Helper ───────────────────────────────────────────────────────

    /**
     * Generic Excel sheet reader.
     * Row 0 is assumed to be a header row and is skipped.
     * Returns all data rows as a 2D String array.
     */
    private Object[][] readSheetData(String sheetName) throws IOException {
        ExcelUtils xlUtil = new ExcelUtils(EXCEL_PATH);

        int totalRows = xlUtil.getRowCount(sheetName);   // last data row index (0-based, excludes header)
        int totalCols = xlUtil.getCellCount(sheetName, 0); // column count from row 0 (header)

        // totalRows from getRowCount() returns the index of the last row (0-based),
        // so the actual count of data rows = totalRows (header is row 0, data starts at row 1).
        String[][] data = new String[totalRows][totalCols];

        for (int i = 1; i <= totalRows; i++) {
            for (int j = 0; j < totalCols; j++) {
                data[i - 1][j] = xlUtil.getCellData(sheetName, i, j);
            }
        }
        return data;
    }
}