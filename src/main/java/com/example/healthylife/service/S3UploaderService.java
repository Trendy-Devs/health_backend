package com.example.healthylife.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.apache.logging.log4j.MarkerManager.clear;

//exception 에러메세지 다시 작성하기!
@Slf4j
@RequiredArgsConstructor
@Service
public class S3UploaderService {
    private final AmazonS3 amazonS3;
    private Set<String> uploadedFileNames = new HashSet<>();
    private Set<Long> uploadedFileSizes = new HashSet<>();

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxSizeString;

    //여러개의 파일 저장
    public List<String> saveFiles(List<MultipartFile> multipartFiles) {
        List<String> uploadedUrls = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (isDuplicate(multipartFile)) {
                throw new RuntimeException("Duplicate file found!!" + multipartFile.getOriginalFilename());
            }
            String uploadedUrl = saveFile(multipartFile);
            uploadedUrls.add(uploadedUrl);
        }
        clear();
        return uploadedUrls;
    }
    //파일 삭제
    public void deleteFile(String fileUrl){
        String[] urlParts = fileUrl.split("/");
        String fileBucket = urlParts[2].split("/")[0];

        if (!fileBucket.equals(bucket)){
            throw new RuntimeException("fileUrl");
        }
        String objectKey = String.join("/", Arrays.copyOfRange(urlParts,3,urlParts.length));
        if (!amazonS3.doesObjectExist(bucket,objectKey)){
            throw new RuntimeException("fileUrl");

        }
        try{
            amazonS3.deleteObject(bucket,objectKey);
        }catch (AmazonS3Exception e){
            log.error("file delete fail :" + e.getMessage());
            throw new RuntimeException();
        }catch (SdkClientException e){
            log.error("AWS SKD Client error" + e.getMessage());
            throw new RuntimeException();
        }
        log.info("File delete complete!!!!" + objectKey);
    }

    // 단일 파일 저장
    public String saveFile(MultipartFile file) {
        String randomFilename = generateRandomFilename(file);

        log.info("File upload started: " + randomFilename);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(bucket, randomFilename, file.getInputStream(), metadata);
        } catch (AmazonS3Exception e) {
            log.error("Amazon S3 error while uploading file: " + e.getMessage());
            throw new RuntimeException();
        } catch (SdkClientException e) {
            log.error("AWS SDK client error while uploading file: " + e.getMessage());
            throw new RuntimeException();
        } catch (IOException e) {
            log.error("IO error while uploading file: " + e.getMessage());
            throw new RuntimeException();
        }

        log.info("File upload completed: " + randomFilename);

        return amazonS3.getUrl(bucket, randomFilename).toString();
    }


    // 요청에 중복되는 파일 여부 확인
    private boolean isDuplicate(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        Long fileSize = multipartFile.getSize();

        if (uploadedFileNames.contains(fileName) && uploadedFileSizes.contains(fileSize)) {
            return true;
        }

        uploadedFileNames.add(fileName);
        uploadedFileSizes.add(fileSize);

        return false;
    }

    private void clear() {
        uploadedFileNames.clear();
        uploadedFileSizes.clear();
    }

    // 랜덤파일명 생성 (파일명 중복 방지)
    private String generateRandomFilename(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileExtension = validateFileExtension(originalFilename);
        String randomFilename = UUID.randomUUID() + "." + fileExtension;
        return randomFilename;
    }

    // 파일 확장자 체크
    private String validateFileExtension(String originalFilename) {
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtensions = Arrays.asList("jpg", "png", "gif", "jpeg");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new RuntimeException("file");
        }
        return fileExtension;
    }
}
