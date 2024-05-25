package com.dalv.bksims.validations;

import com.dalv.bksims.exceptions.FileTooLargeException;
import com.dalv.bksims.exceptions.InvalidFileExtensionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ActivityValidator {
    private static final Set<String> VALID_BANNER_FILE_EXTENSIONS = Set.of(".png", ".jpg", ".jpeg");
    private static final Set<String> VALID_REGULATIONS_FILE_EXTENSIONS = Set.of(".pdf", ".docx");
    private static final Set<String> VALID_EVIDENCE_FILE_EXTENSIONS = Set.of(".png", ".jpg", ".jpeg", ".pdf", ".docx");

    public static void validateBannerFile(MultipartFile bannerFile) throws InvalidFileExtensionException, FileTooLargeException {
        FileValidator.validateFileSize(bannerFile);

        String bannerFileExtension = FileValidator.getFileExtension(bannerFile.getOriginalFilename());
        if (bannerFileExtension == null || !VALID_BANNER_FILE_EXTENSIONS.contains(bannerFileExtension.toLowerCase())) {
            throw new InvalidFileExtensionException("The system only supports .png, .jpg, .jpeg extension for the banner file");
        }
    }

    public static void validateRegulationsFile(MultipartFile regulationsFile) throws InvalidFileExtensionException, FileTooLargeException {
        FileValidator.validateFileSize(regulationsFile);

        String regulationsFileExtension = FileValidator.getFileExtension(regulationsFile.getOriginalFilename());
        if (regulationsFileExtension == null || !VALID_REGULATIONS_FILE_EXTENSIONS.contains(regulationsFileExtension.toLowerCase())) {
            throw new InvalidFileExtensionException("The system only supports .pdf, .docx extension for the regulations file");
        }
    }

    public static void validateEvidenceFile(MultipartFile evidenceFile) throws InvalidFileExtensionException, FileTooLargeException {
        FileValidator.validateFileSize(evidenceFile);

        String evidenceFileExtension = FileValidator.getFileExtension(evidenceFile.getOriginalFilename());
        if (evidenceFileExtension == null || !VALID_EVIDENCE_FILE_EXTENSIONS.contains(evidenceFileExtension.toLowerCase())) {
            throw new InvalidFileExtensionException("The system only supports .pdf, .docx extension for the regulations file");
        }
    }
}
