package com.hcmut.lms.coursemanagement.util;

import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionExportData;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionImportData;
import com.hcmut.lms.coursemanagement.application.dto.response.SubjectImportData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {

    // Column headers for import
    public static final String COL_SUBJECT_CODE = "Subject Code";
    public static final String COL_CLASS_CODE = "Class Code";
    public static final String COL_SECTION_NAME = "Section Name";
    public static final String COL_STATUS = "Status";
    public static final String COL_MAX_STUDENTS = "Max Students";
    public static final String COL_TEACHER_CODE = "Teacher Code";

    // Additional column headers for export only
    public static final String COL_SUBJECT_NAME = "Subject Name";
    public static final String COL_CREDITS = "Credits";
    public static final String COL_SEMESTER_CODE = "Semester Code";
    public static final String COL_CURRENT_STUDENTS = "Current Students";
    public static final String COL_TEACHER_ID = "Teacher ID";
    public static final String COL_TEACHER_NAME = "Teacher Name";

    public static byte[] exportSubjectsWithClassSectionsToExcel(List<ClassSectionExportData> data) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Subjects and Classes");

            // Create header row
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);

            String[] headers = {
                    COL_SUBJECT_CODE, COL_SUBJECT_NAME, COL_CREDITS,
                    COL_CLASS_CODE, COL_SECTION_NAME, COL_SEMESTER_CODE,
                    COL_STATUS, COL_MAX_STUDENTS, COL_CURRENT_STUDENTS, COL_TEACHER_ID, COL_TEACHER_NAME
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }

            // Fill data rows
            int rowNum = 1;
            for (ClassSectionExportData item : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(nullToEmpty(item.getSubjectCode()));
                row.createCell(1).setCellValue(nullToEmpty(item.getSubjectName()));
                row.createCell(2).setCellValue(item.getCredits() != null ? item.getCredits() : 0);
                row.createCell(3).setCellValue(nullToEmpty(item.getClassCode()));
                row.createCell(4).setCellValue(nullToEmpty(item.getSectionName()));
                row.createCell(5).setCellValue(nullToEmpty(item.getSemesterCode()));
                row.createCell(6).setCellValue(nullToEmpty(item.getStatus()));
                row.createCell(7).setCellValue(item.getMaxStudents() != null ? item.getMaxStudents() : 0);
                row.createCell(8).setCellValue(item.getCurrentStudents() != null ? item.getCurrentStudents() : 0);
                row.createCell(9).setCellValue(nullToEmpty(item.getTeacherId()));
                row.createCell(10).setCellValue(nullToEmpty(item.getTeacherName()));
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to export data to Excel", e);
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

    private static String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    // ============================================
    // IMPORT METHODS
    // ============================================

    /**
     * Parse subjects from Excel file
     * Expected columns: Code, Name, Credits, Description
     */
    public static List<SubjectImportData> parseSubjectsFromExcel(org.springframework.web.multipart.MultipartFile file) {
        List<SubjectImportData> subjects = new ArrayList<>();

        try (java.io.InputStream is = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new RuntimeException("Excel file has no sheet");
            }

            // Get header row to map columns
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Excel file has no header row");
            }

            // Map column indices - use specific matching to avoid confusion
            // (e.g., "Subject Code" vs "Semester Code")
            int codeCol = -1, nameCol = -1, creditsCol = -1, descriptionCol = -1;
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    String header = getCellValueAsString(cell).trim().toLowerCase();
                    // Match "subject code" or just "code" (but not "semester code", "class code")
                    if (header.equals("code") || header.equals("mã") ||
                            header.contains("subject code") || header.contains("mã môn")) {
                        codeCol = i;
                    } else if (header.equals("name") || header.equals("tên") ||
                            header.contains("subject name") || header.contains("tên môn")) {
                        nameCol = i;
                    } else if (header.contains("credit") || header.contains("tín chỉ")) {
                        creditsCol = i;
                    } else if (header.contains("description") || header.contains("mô tả")) {
                        descriptionCol = i;
                    }
                }
            }

            // Read data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                SubjectImportData data = SubjectImportData.builder()
                        .rowNumber(i + 1) // 1-indexed for user display
                        .code(codeCol >= 0 ? getCellValueAsString(row.getCell(codeCol)) : null)
                        .name(nameCol >= 0 ? getCellValueAsString(row.getCell(nameCol)) : null)
                        .credits(creditsCol >= 0 ? getCellValueAsInteger(row.getCell(creditsCol)) : null)
                        .description(descriptionCol >= 0 ? getCellValueAsString(row.getCell(descriptionCol)) : null)
                        .build();

                subjects.add(data);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file", e);
        }

        return subjects;
    }

    /**
     * Parse class sections from Excel file (import format - 6 columns)
     * Columns: Subject Code, Class Code, Section Name, Status, Max Students,
     * Teacher Code
     */
    public static List<ClassSectionImportData> parseClassSectionsFromExcel(
            org.springframework.web.multipart.MultipartFile file) {
        List<ClassSectionImportData> classSections = new ArrayList<>();

        try (java.io.InputStream is = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new RuntimeException("Excel file has no sheet");
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Excel file has no header row");
            }

            // Map column indices for 6 import columns
            int subjectCodeCol = -1, classCodeCol = -1, sectionNameCol = -1;
            int statusCol = -1, maxStudentsCol = -1, teacherCodeCol = -1;

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    String header = getCellValueAsString(cell).trim().toLowerCase();
                    if (header.contains("subject code") || header.equals("mã môn") || header.equals("mã môn học")) {
                        subjectCodeCol = i;
                    } else if (header.contains("class code") || header.equals("mã lớp")) {
                        classCodeCol = i;
                    } else if (header.contains("section name") || header.contains("tên lớp")) {
                        sectionNameCol = i;
                    } else if (header.contains("status") || header.contains("trạng thái")) {
                        statusCol = i;
                    } else if (header.contains("max student") || header.contains("sĩ số tối đa")) {
                        maxStudentsCol = i;
                    } else if (header.contains("teacher code") || header.contains("mã giảng viên")) {
                        teacherCodeCol = i;
                    }
                }
            }

            // Read data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                ClassSectionImportData data = ClassSectionImportData.builder()
                        .rowNumber(i + 1)
                        .subjectCode(subjectCodeCol >= 0 ? getCellValueAsString(row.getCell(subjectCodeCol)) : null)
                        .classCode(classCodeCol >= 0 ? getCellValueAsString(row.getCell(classCodeCol)) : null)
                        .sectionName(sectionNameCol >= 0 ? getCellValueAsString(row.getCell(sectionNameCol)) : null)
                        .status(statusCol >= 0 ? getCellValueAsString(row.getCell(statusCol)) : null)
                        .maxStudents(maxStudentsCol >= 0 ? getCellValueAsInteger(row.getCell(maxStudentsCol)) : null)
                        .teacherCode(teacherCodeCol >= 0 ? getCellValueAsString(row.getCell(teacherCodeCol)) : null)
                        .build();

                classSections.add(data);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file", e);
        }

        return classSections;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                // Handle numeric values that should be strings (like codes)
                double numericValue = cell.getNumericCellValue();
                if (numericValue == Math.floor(numericValue)) {
                    return String.valueOf((long) numericValue);
                }
                return String.valueOf(numericValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return null;
        }
    }

    private static Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private static boolean isRowEmpty(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}
