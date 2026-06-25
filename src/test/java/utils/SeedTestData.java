package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

/**
 * SeedTestData — Standalone utility to programmatically write test data into
 * TestData.xlsx so that both SignUp_Data and Login_Data sheets are fully
 * populated for the TestNG automation suite.
 *
 * Run this class once with: java -cp <classpath> SeedTestData
 * OR run via Maven exec plugin.
 *
 * The script is idempotent — it recreates sheets if they already exist.
 */
public class SeedTestData {

    private static final String EXCEL_PATH =
        System.getProperty("user.dir") + "/src/test/resources/TestData.xlsx";

    public static void main(String[] args) throws IOException {
        File file = new File(EXCEL_PATH);

        XSSFWorkbook workbook;

        // Open existing workbook or create new one
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(fis);
            }
        } else {
            workbook = new XSSFWorkbook();
        }

        writeSignUpData(workbook);
        writeLoginData(workbook);

        // Save workbook
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }

        workbook.close();
        System.out.println("✅ TestData.xlsx has been seeded successfully at: " + EXCEL_PATH);
    }

    // ─── SignUp_Data Sheet ─────────────────────────────────────────────────────
    // Columns: testCaseID | userName | email | password | role | expectedResult | expectedMessage

    private static void writeSignUpData(XSSFWorkbook workbook) {
        // Remove sheet if it already exists, then re-create
        int sheetIndex = workbook.getSheetIndex("SignUp_Data");
        if (sheetIndex >= 0) {
            workbook.removeSheetAt(sheetIndex);
        }
        Sheet sheet = workbook.createSheet("SignUp_Data");

        CellStyle headerStyle = createHeaderStyle(workbook);

        // ── Header Row ──
        String[] headers = {
            "TestCaseID", "UserName", "Email", "Password", "Role", "ExpectedResult", "ExpectedMessage"
        };
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ── Data Rows ──
        // Format: { testCaseID, userName, email, password, role, expectedResult, expectedMessage }
        Object[][] signUpData = {
            // ── POSITIVE TEST CASES ──
            // UM_TC_01: Valid signup as Brand
            { "UM_TC_01", "BrandUser", "brandtest@gmail.com", "Pass@12345", "Brand",
              "Success", "Registration successful!" },

            // UM_TC_02: Valid signup as Influencer
            { "UM_TC_02", "InfluencerUser", "influencertest@gmail.com", "Pass@12345", "Influencer",
              "Success", "Registration successful!" },

            // ── NEGATIVE TEST CASES ──
            // UM_TC_03: Duplicate email — email is already registered (uses a seeded account)
            { "UM_TC_03", "UniqueUser03", "brand@gmail.com", "Pass@12345", "Brand",
              "Failure", "Email already registered" },

            // UM_TC_04: Duplicate username — the seeded brand username is "brand"
            { "UM_TC_04", "brand", "unique04@gmail.com", "Pass@12345", "Brand",
              "Failure", "Username already exists" },

            // UM_TC_05: Invalid email format
            { "UM_TC_05", "TestUser05", "not-a-valid-email", "Pass@12345", "Brand",
              "Failure", "Invalid email format" },

            // UM_TC_06: Weak password (no '@' symbol and no digit — fails the pattern /^(?=.*[A-Za-z])(?=.*\d)(?=.*@).{6,}$/)
            { "UM_TC_06", "TestUser06", "testuser06@gmail.com", "weakpassword", "Brand",
              "Failure", "Password must" },

            // UM_TC_07: Username too short (< 3 characters — Angular minLength(3) validator)
            { "UM_TC_07", "ab", "testuser07@gmail.com", "Pass@12345", "Brand",
              "Failure", "at least 3" },

            // UM_TC_08: All fields empty — clicking submit triggers required validators
            { "UM_TC_08", "", "", "", "",
              "Failure", "required" }
        };

        int rowNum = 1;
        for (Object[] row : signUpData) {
            Row dataRow = sheet.createRow(rowNum++);
            for (int j = 0; j < row.length; j++) {
                dataRow.createCell(j).setCellValue(String.valueOf(row[j]));
            }
        }

        // Auto-size columns for readability
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        System.out.println("  → SignUp_Data sheet written (" + (rowNum - 1) + " test rows)");
    }

    // ─── Login_Data Sheet ──────────────────────────────────────────────────────
    // Columns: testCaseID | email | password | expectedResult | expectedMessage

    private static void writeLoginData(XSSFWorkbook workbook) {
        int sheetIndex = workbook.getSheetIndex("Login_Data");
        if (sheetIndex >= 0) {
            workbook.removeSheetAt(sheetIndex);
        }
        Sheet sheet = workbook.createSheet("Login_Data");

        CellStyle headerStyle = createHeaderStyle(workbook);

        // ── Header Row ──
        String[] headers = {
            "TestCaseID", "Email", "Password", "ExpectedResult", "ExpectedMessage"
        };
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ── Data Rows ──
        // Format: { testCaseID, email, password, expectedResult, expectedMessage }
        Object[][] loginData = {
            // ── POSITIVE TEST CASES ──
            // UM_TC_09: Valid Brand login
            { "UM_TC_09", "brand@gmail.com", "brand@123", "Success", "Login successful!" },

            // UM_TC_10: Valid Influencer login
            { "UM_TC_10", "influencer@gmail.com", "influencer@123", "Success", "Login successful!" },

            // ── NEGATIVE TEST CASES ──
            // UM_TC_11: Correct email, wrong password
            { "UM_TC_11", "brand@gmail.com", "WrongPass@999", "Failure", "Invalid email or password" },

            // UM_TC_12: Unregistered email address
            { "UM_TC_12", "notregistered@gmail.com", "Pass@12345", "Failure", "Invalid email or password" },

            // UM_TC_13: Empty email field — should trigger Angular required validator
            { "UM_TC_13", "", "Pass@12345", "Failure", "required" },

            // UM_TC_14: Empty password field — should trigger Angular required validator
            { "UM_TC_14", "brand@gmail.com", "", "Failure", "required" }
        };

        int rowNum = 1;
        for (Object[] row : loginData) {
            Row dataRow = sheet.createRow(rowNum++);
            for (int j = 0; j < row.length; j++) {
                dataRow.createCell(j).setCellValue(String.valueOf(row[j]));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        System.out.println("  → Login_Data sheet written (" + (rowNum - 1) + " test rows)");
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private static CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
