package com.example.workershekel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class ExcelExporter {

    public static void exportMonthlyReport(File file, int year, int month,
                                           List<Worker> workers,
                                           List<AttendanceRecord> attendanceList,
                                           List<FinanceRecord> financeList) throws Exception {

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("דוח חודשי - " + month + "-" + year);
            sheet.setRightToLeft(true);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            setBorders(headerStyle);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            setBorders(dataStyle);

            CellStyle weekendStyle = workbook.createCellStyle();
            weekendStyle.setAlignment(HorizontalAlignment.CENTER);
            weekendStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            weekendStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setBorders(weekendStyle);

            CellStyle summaryStyle = workbook.createCellStyle();
            Font summaryFont = workbook.createFont();
            summaryFont.setBold(true);
            summaryStyle.setFont(summaryFont);
            summaryStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
            summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            summaryStyle.setAlignment(HorizontalAlignment.CENTER);
            setBorders(summaryStyle);

            Row headerRow = sheet.createRow(0);
            Cell cDate = headerRow.createCell(0);
            cDate.setCellValue("תאריך");
            cDate.setCellStyle(headerStyle);

            int colIndex = 1;
            for (Worker w : workers) {
                Cell c = headerRow.createCell(colIndex++);
                c.setCellValue(w.getName());
                c.setCellStyle(headerStyle);
            }

            int expenseColIndex = colIndex++;
            Cell cExpense = headerRow.createCell(expenseColIndex);
            cExpense.setCellValue("הוצאות");
            cExpense.setCellStyle(headerStyle);

            int incomeColIndex = colIndex++;
            Cell cIncome = headerRow.createCell(incomeColIndex);
            cIncome.setCellValue("הכנסות");
            cIncome.setCellStyle(headerStyle);

            YearMonth ym = YearMonth.of(year, month);
            int daysInMonth = ym.lengthOfMonth();

            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate currentDate = LocalDate.of(year, month, day);
                boolean isWeekend = currentDate.getDayOfWeek().name().equals("FRIDAY") ||
                        currentDate.getDayOfWeek().name().equals("SATURDAY");
                CellStyle rowCellStyle = isWeekend ? weekendStyle : dataStyle;

                Row row = sheet.createRow(day);
                Cell dateCell = row.createCell(0);
                dateCell.setCellValue(String.format("%02d/%02d/%d", day, month, year));
                dateCell.setCellStyle(rowCellStyle);

                for (int i = 0; i < workers.size(); i++) {
                    Worker w = workers.get(i);
                    double val = 0.0;
                    for (AttendanceRecord a : attendanceList) {
                        if (a.getWorkerName().equals(w.getName()) && a.getDate().equals(currentDate)) {
                            if (a.getStatus().equals("נוכח") || a.getStatus().equals("حاضر")) val = 1.0;
                            else if (a.getStatus().equals("חצי יום") || a.getStatus().equals("نصف يوم")) val = 0.5;
                        }
                    }
                    Cell cell = row.createCell(i + 1);
                    cell.setCellValue(val);
                    cell.setCellStyle(rowCellStyle);
                }

                double dailyExpense = 0.0;
                double dailyIncome = 0.0;
                for (FinanceRecord f : financeList) {
                    if (f.getDate().equals(currentDate)) {
                        if (f.getType().equals("مصروف") || f.getType().equals("הוצאה")) {
                            dailyExpense += f.getAmount();
                        } else if (f.getType().equals("إيراد") || f.getType().equals("הכנסה")) {
                            dailyIncome += f.getAmount();
                        }
                    }
                }

                Cell expenseCell = row.createCell(expenseColIndex);
                expenseCell.setCellValue(dailyExpense);
                expenseCell.setCellStyle(rowCellStyle);

                Cell incomeCell = row.createCell(incomeColIndex);
                incomeCell.setCellValue(dailyIncome);
                incomeCell.setCellStyle(rowCellStyle);
            }

            int daysRowIdx = daysInMonth + 1;
            int wageRowIdx = daysInMonth + 2;
            int totalRowIdx = daysInMonth + 3;

            Row daysRow = sheet.createRow(daysRowIdx);
            Row wageRow = sheet.createRow(wageRowIdx);
            Row totalRow = sheet.createRow(totalRowIdx);

            Cell lblDays = daysRow.createCell(0);
            lblDays.setCellValue("ימי עבודה");
            lblDays.setCellStyle(summaryStyle);

            Cell lblWage = wageRow.createCell(0);
            lblWage.setCellValue("יומי");
            lblWage.setCellStyle(summaryStyle);

            Cell lblTotal = totalRow.createCell(0);
            lblTotal.setCellValue("סך הכל (₪)");
            lblTotal.setCellStyle(summaryStyle);

            for (int i = 0; i < workers.size(); i++) {
                int col = i + 1;
                String colLetter = getColumnLetter(col);

                Cell cSumDays = daysRow.createCell(col);
                cSumDays.setCellFormula("SUM(" + colLetter + "2:" + colLetter + (daysInMonth + 1) + ")");
                cSumDays.setCellStyle(summaryStyle);

                Cell cDailyWage = wageRow.createCell(col);
                cDailyWage.setCellValue(workers.get(i).getDailyWage());
                cDailyWage.setCellStyle(summaryStyle);

                Cell cTotalPay = totalRow.createCell(col);
                cTotalPay.setCellFormula(colLetter + (daysRowIdx + 1) + "*" + colLetter + (wageRowIdx + 1));
                cTotalPay.setCellStyle(summaryStyle);
            }

            String expenseColLetter = getColumnLetter(expenseColIndex);
            Cell cExpenseTotal = totalRow.createCell(expenseColIndex);
            cExpenseTotal.setCellFormula("SUM(" + expenseColLetter + "2:" + expenseColLetter + (daysInMonth + 1) + ")");
            cExpenseTotal.setCellStyle(summaryStyle);

            String incomeColLetter = getColumnLetter(incomeColIndex);
            Cell cIncomeTotal = totalRow.createCell(incomeColIndex);
            cIncomeTotal.setCellFormula("SUM(" + incomeColLetter + "2:" + incomeColLetter + (daysInMonth + 1) + ")");
            cIncomeTotal.setCellStyle(summaryStyle);

            for (int i = 0; i <= incomeColIndex; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 800);
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    private static void setBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private static String getColumnLetter(int colIndex) {
        StringBuilder sb = new StringBuilder();
        while (colIndex >= 0) {
            sb.insert(0, (char) ('A' + (colIndex % 26)));
            colIndex = (colIndex / 26) - 1;
        }
        return sb.toString();
    }
}