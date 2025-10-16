package com.hcmut.lms.usermanagement.util;

import com.hcmut.lms.usermanagement.exception.ExcelProcessingException;
import com.hcmut.lms.usermanagement.model.entity.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {
    
    // Template column names
    public static final String COL_EMAIL = "Email";
    public static final String COL_FULL_NAME = "Full Name";
    public static final String COL_PHONE = "Phone";
    public static final String COL_ADDRESS = "Address";
    public static final String COL_DEPARTMENT = "Department";
    public static final String COL_STUDENT_ID = "Student ID";
    public static final String COL_ROLES = "Roles";
    
    public static byte[] generateTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            String[] headers = {COL_EMAIL, COL_FULL_NAME, COL_PHONE, COL_ADDRESS, COL_DEPARTMENT, COL_STUDENT_ID, COL_ROLES};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4000);
            }
            
            // Add sample data
            Row sampleRow = sheet.createRow(1);
            sampleRow.createCell(0).setCellValue("student@hcmut.edu.vn");
            sampleRow.createCell(1).setCellValue("Nguyen Van A");
            sampleRow.createCell(2).setCellValue("0901234567");
            sampleRow.createCell(3).setCellValue("Ho Chi Minh City");
            sampleRow.createCell(4).setCellValue("Computer Science");
            sampleRow.createCell(5).setCellValue("2110001");
            sampleRow.createCell(6).setCellValue("STUDENT");
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            throw new ExcelProcessingException("Failed to generate template", e);
        }
    }
    
    public static List<UserImportData> parseExcelFile(MultipartFile file) {
        List<UserImportData> users = new ArrayList<>();
        
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Read header row to get column indices
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new ExcelProcessingException("Excel file is empty");
            }
            
            // Start from row 1 (skip header)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }
                
                UserImportData userData = new UserImportData();
                userData.setRowNumber(i + 1);
                userData.setEmail(getCellValueAsString(row.getCell(0)));
                userData.setFullName(getCellValueAsString(row.getCell(1)));
                userData.setPhone(getCellValueAsString(row.getCell(2)));
                userData.setAddress(getCellValueAsString(row.getCell(3)));
                userData.setDepartment(getCellValueAsString(row.getCell(4)));
                userData.setStudentId(getCellValueAsString(row.getCell(5)));
                userData.setRoles(getCellValueAsString(row.getCell(6)));
                
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
            
            String[] headers = {COL_EMAIL, COL_FULL_NAME, COL_PHONE, COL_ADDRESS, COL_DEPARTMENT, COL_STUDENT_ID, "Status"};
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
                row.createCell(0).setCellValue(user.getEmail());
                row.createCell(1).setCellValue(user.getFullName());
                row.createCell(2).setCellValue(user.getPhone() != null ? user.getPhone() : "");
                row.createCell(3).setCellValue(user.getAddress() != null ? user.getAddress() : "");
                row.createCell(4).setCellValue(user.getDepartment() != null ? user.getDepartment() : "");
                row.createCell(5).setCellValue(user.getStudentId() != null ? user.getStudentId() : "");
                row.createCell(6).setCellValue(user.getStatus().toString());
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
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }
    
    private static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }
    
    public static class UserImportData {
        private int rowNumber;
        private String email;
        private String fullName;
        private String phone;
        private String address;
        private String department;
        private String studentId;
        private String roles;
        
        // Getters and setters
        public int getRowNumber() { return rowNumber; }
        public void setRowNumber(int rowNumber) { this.rowNumber = rowNumber; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        
        public String getRoles() { return roles; }
        public void setRoles(String roles) { this.roles = roles; }
    }
}

