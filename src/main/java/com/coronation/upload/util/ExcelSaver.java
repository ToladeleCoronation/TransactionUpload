package com.coronation.upload.util;

import com.coronation.upload.domain.DataColumn;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

    private static void createColumns(Workbook workbook, Sheet sheet, List<List<String>> dataList) {
        CellStyle style = workbook.createCellStyle();

        style.setWrapText(true);

        int index = 1;
        for (List<String> data: dataList) {
            Row row = sheet.createRow(index);
            for (int i = 0; i < data.size(); i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(data.get(i));
                cell.setCellStyle(style);
            }
            ++index;
        }
    }
}
