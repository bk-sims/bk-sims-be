package com.dalv.bksims.validations;

import com.dalv.bksims.exceptions.FileTooLargeException;
import com.dalv.bksims.exceptions.InvalidFileExtensionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class CourseProposalValidator {
    private static final Set<String> VALID_EXCEL_FILE_EXTENSIONS = Set.of(".xls", ".xlsx");

    public static void validateExcelFile(MultipartFile excelFile) throws InvalidFileExtensionException, FileTooLargeException {
        FileValidator.validateFileSize(excelFile);

        String excelFileExtension = FileValidator.getFileExtension(excelFile.getOriginalFilename());
        if (excelFileExtension == null || !VALID_EXCEL_FILE_EXTENSIONS.contains(excelFileExtension.toLowerCase())) {
            throw new InvalidFileExtensionException("The system only supports .xls, .xlsx extension for the excel file");
        }
    }
}
