package com.dalv.bksims.services.common;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final AmazonS3 s3Client;

    @Value("${s3.bucket.name}")
    private String bucketName;

    @Value("${s3.bucket.url}")
    private String bucketUrl;

    public String uploadFileForActivity(MultipartFile file, String organizationName) {
        File fileObj = convertMultiPartFileToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        log.info(bucketName, fileName, fileObj);

        // new file will be added in /organizationName folder
        s3Client.putObject(new PutObjectRequest(bucketName, organizationName + "/" + fileName, fileObj));
        fileObj.delete();
        return fileName;
    }

    public String uploadFileForActivityEvidence(MultipartFile file) {
        File fileObj = convertMultiPartFileToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        log.info(bucketName, fileName, fileObj);

        // new file will be added in /activity_evidence folder
        s3Client.putObject(new PutObjectRequest(bucketName,  "activity_evidence/" + fileName, fileObj));
        fileObj.delete();
        return fileName;
    }

    public void deleteFileForActivity(String fileURL) {
        AmazonS3URI fileURI = new AmazonS3URI(fileURL);
        String keyName = fileURI.getKey();
        System.out.println("Deleting object from bucket: " + bucketName + ", key: " + keyName);

        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, keyName));
//            s3Client.deleteObject(new DeleteObjectRequest(bucketName, "School+Union%2F1704605470427_2542ce67cf19969950be8014948ada3d.jpg"));
            System.out.println("Object deleted successfully.");
        } catch (Exception e) {
            System.err.println("Error deleting object: " + e.getMessage());
        }
    }


    public String getFileUrl(String fileName, String path) {
        return bucketUrl + path + fileName;
    }

    public S3ObjectInputStream findFileByName(String organizationName, String fileName) {
        log.info("Downloading file with name {}", fileName);
        return s3Client.getObject(bucketName, organizationName + "/" + fileName).getObjectContent();
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }
}

