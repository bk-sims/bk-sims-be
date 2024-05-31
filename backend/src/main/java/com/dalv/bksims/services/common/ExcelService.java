package com.dalv.bksims.services.common;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.dalv.bksims.models.dtos.course_registration.CourseProposalDto;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelService {
    public List<CourseProposalDto> readExcel(S3ObjectInputStream file) {
        List<CourseProposalDto> courseProposalDtos = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();
        try (Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            rows.next(); // skip header
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                CourseProposalDto courseProposalDto = CourseProposalDto.builder()
                        .courseCode(formatter.formatCellValue(currentRow.getCell(0)))
                        .className(formatter.formatCellValue(currentRow.getCell(1)))
                        .campus(formatter.formatCellValue(currentRow.getCell(2)))
                        .room(formatter.formatCellValue(currentRow.getCell(3)))
                        .weeks(formatter.formatCellValue(currentRow.getCell(4)))
                        .days(formatter.formatCellValue(currentRow.getCell(5)))
                        .startTime(formatter.formatCellValue(currentRow.getCell(6)))
                        .endTime(formatter.formatCellValue(currentRow.getCell(7)))
                        .type(formatter.formatCellValue(currentRow.getCell(8)))
                        .capacity(Integer.parseInt(formatter.formatCellValue(currentRow.getCell(9))))
                        .lecturerCode(formatter.formatCellValue(currentRow.getCell(10)))
                        .build();

                courseProposalDtos.add(courseProposalDto);
            }
        } catch (IOException e) {
            throw new RuntimeException("FAIL! -> message = " + e.getMessage());
        }

        return courseProposalDtos;
    }
}
