package com.elasticsearch.ElasticSearch.util;

import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.elasticsearch.ElasticSearch.entity.Province;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtil {
    public static List<Province> readProvinceExcel(String filePath) throws IOException {
        FileInputStream excelFile = new FileInputStream(filePath);
        Workbook workbook = WorkbookFactory.create(excelFile);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();

        List<Province> provinces = new ArrayList<>();

        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            Cell provinceIdCell = currentRow.getCell(0);
            Cell provinceNameCell = currentRow.getCell(1);

            if (provinceIdCell != null && provinceNameCell != null) {
                Province province = new Province();
                province.setProvinceId(provinceIdCell.getStringCellValue());
                province.setProvinceName(provinceNameCell.getStringCellValue());

                provinces.add(province);
            }
        }
        workbook.close();
        return provinces;
    }
}
