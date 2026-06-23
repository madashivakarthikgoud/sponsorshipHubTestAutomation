package dataProviders;

import org.testng.annotations.DataProvider;
import utils.ExcelUtils;

import java.io.IOException;

public class TestDataProviders {

    private static final String EXCEL_PATH = System.getProperty("user.dir") + "/src/test/resources/TestData.xlsx";

    @DataProvider(name = "signUpData")
    public Object[][] getSignUpData() throws IOException {
        return readSheetData("SignUp_Data");
    }

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() throws IOException {
        return readSheetData("Login_Data");
    }

    private Object[][] readSheetData(String sheetName) throws IOException {
        ExcelUtils xlUtil = new ExcelUtils(EXCEL_PATH);

        int totalRows = xlUtil.getRowCount(sheetName);
        int totalCols = xlUtil.getCellCount(sheetName, 0);
        String[][] data = new String[totalRows][totalCols];

        for (int i = 1; i <= totalRows; i++) {
            for (int j = 0; j < totalCols; j++) {
                // We store in i-1 because the array starts at index 0
                data[i - 1][j] = xlUtil.getCellData(sheetName, i, j);
            }
        }
        return data;
    }
}