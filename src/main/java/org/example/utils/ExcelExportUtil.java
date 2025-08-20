// org.example.util.ExcelExportUtil
package org.example.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.pojo.Tasks;

import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelExportUtil {

    public static void writeTasks(List<Tasks> list, OutputStream out) throws Exception {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("任务列表");

            String[] headers = {"ID","标题","描述","优先级","截止时间","状态","创建时间","更新时间"};
            Row head = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) head.createCell(i).setCellValue(headers[i]);

            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (int r = 0; r < list.size(); r++) {
                Tasks t = list.get(r);
                Row row = sheet.createRow(r + 1);
                row.createCell(0).setCellValue(t.getId());
                row.createCell(1).setCellValue(nvl(t.getTitle()));
                row.createCell(2).setCellValue(nvl(t.getDescription()));
                row.createCell(3).setCellValue(t.getPriority() == null ? 0 : t.getPriority());
                row.createCell(4).setCellValue(t.getDeadline()==null ? "" : t.getDeadline().format(f));
                row.createCell(5).setCellValue(t.getStatus() == null ? 0 : t.getStatus());
                row.createCell(6).setCellValue(t.getCreatedAt()==null ? "" : t.getCreatedAt().format(f));
                row.createCell(7).setCellValue(t.getUpdatedAt()==null ? "" : t.getUpdatedAt().format(f));
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            wb.write(out);
        }
    }

    private static String nvl(String s) { return s == null ? "" : s; }
}
