package com.hcmut.lms.usermanagement.util;

import com.hcmut.lms.usermanagement.exception.ExcelProcessingException;
import com.hcmut.lms.usermanagement.model.entity.User;
import lombok.Data;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExcelUtil {

    // Template column names
    public static final String COL_EMAIL = "Email";
    public static final String COL_FIRST_NAME = "First Name";
    public static final String COL_LAST_NAME = "Last Name";
    public static final String COL_PHONE = "Phone";
    public static final String COL_SPECIALIZATION_ID = "Specialization ID";
    public static final String COL_ROLE_NAME = "Role Name";
    public static final String COL_STUDENT_CODE = "Student Code";
    public static final String COL_TEACHER_CODE = "Teacher Code";
    public static final String COL_TEACHER_BIO = "Teacher Bio";
    public static final String COL_ADMIN_CODE = "Admin Code";

    public static List<UserImportData> parseExcelFile(MultipartFile file) {
        List<UserImportData> users = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Read header row to get column indices
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new ExcelProcessingException("Excel file is empty");
            }

            // Map column headers to indices
            int emailIdx = -1, firstNameIdx = -1, lastNameIdx = -1, phoneIdx = -1;
            int specializationIdIdx = -1, roleNameIdx = -1, studentCodeIdx = -1;
            int teacherCodeIdx = -1, teacherBioIdx = -1, adminCodeIdx = -1;

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell == null)
                    continue;

                String headerValue;
                try {
                    headerValue = cell.getStringCellValue().trim();
                } catch (Exception ex) {
                    headerValue = cell.toString().trim();
                }
                switch (headerValue) {
                    case COL_EMAIL -> emailIdx = i;
                    case COL_FIRST_NAME -> firstNameIdx = i;
                    case COL_LAST_NAME -> lastNameIdx = i;
                    case COL_PHONE -> phoneIdx = i;
                    case COL_SPECIALIZATION_ID -> specializationIdIdx = i;
                    case COL_ROLE_NAME -> roleNameIdx = i;
                    case COL_STUDENT_CODE -> studentCodeIdx = i;
                    case COL_TEACHER_CODE -> teacherCodeIdx = i;
                    case COL_TEACHER_BIO -> teacherBioIdx = i;
                    case COL_ADMIN_CODE -> adminCodeIdx = i;
                }
            }

            // Validate required columns to avoid row.getCell(-1) -> "Cell index must be >=
            // 0"
            List<String> missing = new ArrayList<>();
            if (emailIdx < 0)
                missing.add(COL_EMAIL);
            if (firstNameIdx < 0)
                missing.add(COL_FIRST_NAME);
            if (lastNameIdx < 0)
                missing.add(COL_LAST_NAME);
            if (roleNameIdx < 0)
                missing.add(COL_ROLE_NAME);
            if (!missing.isEmpty()) {
                throw new ExcelProcessingException(
                        "File import không đúng template. Thiếu cột: " + String.join(", ", missing));
            }

            // Start from row 1 (skip header)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                UserImportData userData = new UserImportData();
                userData.setRowNumber(i + 1);
                userData.setEmail(getCellValueAsString(getCellSafe(row, emailIdx)));
                userData.setFirstName(getCellValueAsString(getCellSafe(row, firstNameIdx)));
                userData.setLastName(getCellValueAsString(getCellSafe(row, lastNameIdx)));
                userData.setPhone(getCellValueAsString(getCellSafe(row, phoneIdx)));

                String specIdStr = getCellValueAsString(getCellSafe(row, specializationIdIdx));
                if (specIdStr != null && !specIdStr.isEmpty()) {
                    try {
                        userData.setSpecializationId(UUID.fromString(specIdStr));
                    } catch (NumberFormatException e) {
                        // Ignore invalid specialization ID
                    }
                }

                userData.setRoleName(getCellValueAsString(getCellSafe(row, roleNameIdx)));
                userData.setStudentCode(getCellValueAsString(getCellSafe(row, studentCodeIdx)));
                userData.setTeacherCode(getCellValueAsString(getCellSafe(row, teacherCodeIdx)));
                userData.setTeacherBio(getCellValueAsString(getCellSafe(row, teacherBioIdx)));
                userData.setAdminCode(getCellValueAsString(getCellSafe(row, adminCodeIdx)));

                users.add(userData);
            }

        } catch (IOException e) {
            throw new ExcelProcessingException("Failed to parse Excel file", e);
        }

        return users;
    }

    public static byte[] exportUsersToExcel(List<User> users) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            // Create header
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);

            String[] headers = {
                    COL_EMAIL, COL_FIRST_NAME, COL_LAST_NAME, COL_PHONE,
                    COL_SPECIALIZATION_ID, COL_ROLE_NAME, COL_STUDENT_CODE,
                    COL_TEACHER_CODE, COL_TEACHER_BIO, COL_ADMIN_CODE
            };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4000);
            }

            // Fill data
            int rowNum = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getEmail() != null ? user.getEmail() : "");
                row.createCell(1).setCellValue(user.getFirstName() != null ? user.getFirstName() : "");
                row.createCell(2).setCellValue(user.getLastName() != null ? user.getLastName() : "");
                row.createCell(3).setCellValue(user.getPhone() != null ? user.getPhone() : "");
                row.createCell(4)
                        .setCellValue(user.getSpecializationId() != null ? user.getSpecializationId().toString() : "");
                row.createCell(5).setCellValue(user.getRole() != null ? user.getRole().getName() : "");
                row.createCell(6)
                        .setCellValue(user.getStudent() != null && user.getStudent().getStudentCode() != null
                                ? user.getStudent().getStudentCode()
                                : "");
                row.createCell(7)
                        .setCellValue(user.getTeacher() != null && user.getTeacher().getTeacherCode() != null
                                ? user.getTeacher().getTeacherCode()
                                : "");
                row.createCell(8)
                        .setCellValue(user.getTeacher() != null && user.getTeacher().getBio() != null
                                ? user.getTeacher().getBio()
                                : "");
                row.createCell(9)
                        .setCellValue(user.getAdmin() != null && user.getAdmin().getAdminCode() != null
                                ? user.getAdmin().getAdminCode()
                                : "");
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new ExcelProcessingException("Failed to export users to Excel", e);
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                } else {
                    // Check if it's a whole number
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        yield String.valueOf((long) numericValue);
                    } else {
                        yield String.valueOf(numericValue);
                    }
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> null;
        };
    }

    private static boolean isRowEmpty(Row row) {
        if (row.getFirstCellNum() < 0) {
            return true;
        }
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private static Cell getCellSafe(Row row, int idx) {
        if (row == null || idx < 0)
            return null;
        return row.getCell(idx);
    }

    @Data
    public static class UserImportData {
        // Getters and setters
        private int rowNumber;
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
        private UUID specializationId;
        private String roleName;
        private String studentCode;
        private String teacherCode;
        private String teacherBio;
        private String adminCode;
    }
}
