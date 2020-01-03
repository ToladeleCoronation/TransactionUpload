package com.coronation.upload.util;

import com.coronation.upload.domain.DataColumn;
import com.coronation.upload.dto.LogExporter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Toyin on 8/9/19.
 */
public class ExcelSaver {

    public static void createLogFile(List<DataColumn> columns, List<List<String>> data, String path)
            throws IOException {
        Workbook workbook = new XSSFWorkbook();

        try {
            Sheet sheet = workbook.createSheet("Logs");
            sheet.setColumnWidth(0, 6000);
            sheet.setColumnWidth(1, 4000);
            createHeader(workbook, sheet, columns);
            createColumns(workbook, sheet, data);

            FileOutputStream outputStream = new FileOutputStream(path);
            workbook.write(outputStream);
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
    }


    public static void createLogFileReport(List<String> columns, List<LogExporter> data, String path)
            throws IOException {
        Workbook workbook = new XSSFWorkbook();

        try {
            Sheet sheet = workbook.createSheet("Logs");
            createLogHeader(workbook, sheet, columns);
            createColumnsLogs(workbook, sheet, data);
            path = path + ".xlsx";
            FileOutputStream outputStream = new FileOutputStream(path);
            System.out.println(path + " this is the path to success");
            workbook.write(outputStream);
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
    }

    private static void createHeader(Workbook workbook, Sheet sheet, List<DataColumn> dataColumns) {
        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();

        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < dataColumns.size(); i++) {
            Cell headerCell = header.createCell(i);
            headerCell.setCellValue(dataColumns.get(i).getName());
            headerCell.setCellStyle(headerStyle);
        }
    }
    private static void createHeaderReport(Workbook workbook, Sheet sheet, List<DataColumn> dataColumns) {
        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();

        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < dataColumns.size(); i++) {
            Cell headerCell = header.createCell(i);
            headerCell.setCellValue(dataColumns.get(i).getName());
            headerCell.setCellStyle(headerStyle);
        }

    }

    private static void createLogHeader(Workbook workbook, Sheet sheet, List<String> logHeader) {
        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(CellStyle.ALIGN_FILL);

        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setBold(true);
        headerStyle.setFont(font);
        int length = 0;

        for (int i = 0; i < logHeader.size(); i++) {
            Cell headerCell = header.createCell(i);
            headerCell.setCellValue(logHeader.get(i));
            length = logHeader.get(i).length();
            headerCell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

    }

    private static void createColumns(Workbook workbook, Sheet sheet, List<List<String>> dataList) {
        CellStyle style = workbook.createCellStyle();

        style.setWrapText(true);

        int index = 1;
        for (List<String> data : dataList) {
            Row row = sheet.createRow(index);
            for (int i = 0; i < data.size(); i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(data.get(i));
                cell.setCellStyle(style);
            }
            ++index;
        }
    }

    private static void createColumnsLogs(Workbook workbook, Sheet sheet, List<LogExporter> dataList) {
        CellStyle style = workbook.createCellStyle();

//        style.setWrapText(true);

        int index = 1;
        for (LogExporter data : dataList) {
            Row row = sheet.createRow(index);

            Cell cell = row.createCell(0);
            cell.setCellValue(data.getAccountNumber());
            cell.setCellStyle(style);


            Cell cell1 = row.createCell(1);
            cell1.setCellValue(data.getPhoneNumber());
            cell1.setCellStyle(style);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(data.getCount());
            cell2.setCellStyle(style);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(data.getAmount());
            cell3.setCellStyle(style);

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(data.getNarration());
            cell4.setCellStyle(style);

            Cell cell5 = row.createCell(5);
            cell5.setCellValue(data.getResponse_code());
            cell5.setCellStyle(style);

            Cell cell6 = row.createCell(6);
            cell6.setCellValue(data.getStatus());
            cell6.setCellStyle(style);

            Cell cell7 = row.createCell(7);
            cell7.setCellValue(data.getDateDebitted());
            cell7.setCellStyle(style);
            sheet.autoSizeColumn(index);
            ++index;
        }

    }
}
