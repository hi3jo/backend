package com.AI.chatbot.util;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.AI.chatbot.exception.AppException;
import com.AI.chatbot.exception.AppException.ErrorType;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3Utils {
    
    @Value("${cloud.aws.s3.bucket}")
	private String bucket;

    @Autowired
    private AmazonS3Client amazonS3;
    
    // 폴더 생성
    public void createFolder(String bucketName, String folderName) {
        
        amazonS3.putObject(bucketName, folderName + "/", new ByteArrayInputStream(new byte[0]), new ObjectMetadata());
    }

    // 다중 파일 업로드
    public void fileUpload(List<MultipartFile> files, List<String> fileList) throws Exception {
        
        if(amazonS3 != null) {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            String today = sdf.format(date);

            if(!files.isEmpty()) {
                createFolder(bucket + "/contact", today);
            }

            ObjectMetadata objectMetadata = new ObjectMetadata();
            for(int i=0; i<files.size(); i++) {
                objectMetadata.setContentType(files.get(i).getContentType());
                objectMetadata.setContentLength(files.get(i).getSize());
                objectMetadata.setHeader("filename", files.get(i).getOriginalFilename());
                amazonS3.putObject(new PutObjectRequest(bucket + "/contact/" + today, fileList.get(i), files.get(i).getInputStream(), objectMetadata));
            }
        } else {
            throw new AppException(ErrorType.aws_credentials_fail, null);
        }
    }

    // 다중 파일 삭제
    public void fileDelete(String filePath, String fileName) {
        
        if(amazonS3 != null) {
            amazonS3.deleteObject(new DeleteObjectRequest(filePath, fileName));
        } else {
            throw new AppException(ErrorType.aws_credentials_fail, null);
        }
    }
}
