package com.dalv.bksims.validations;

import com.dalv.bksims.exceptions.FileTooLargeException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidator {
    private static final long MAX_FILE_SIZE = 3 * 1024 * 1024; // 3MB

    public static void validateFileSize(MultipartFile file) throws FileTooLargeException {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileTooLargeException("File should be less than 3MB in size");
        }
    }

    public static String getFileExtension(String originalFileName) {
        if (originalFileName != null) {
            int dotPos = originalFileName.lastIndexOf(".");
            if (dotPos >= 0) {
                return originalFileName.substring(dotPos);
            }
        }

        return null;
    }
}
