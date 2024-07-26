package com.AI.chatbot.util;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.AI.chatbot.exception.AppException;
import com.AI.chatbot.exception.AppException.ErrorType;
import com.AI.chatbot.model.Post;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
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
    public void fileUpload(List<MultipartFile> files, Post post) throws Exception {
        
        if(amazonS3 != null) {

            ObjectMetadata metadata = null;
            List<String> uptImgList = new ArrayList<>(); // ArrayList 생성
            for (MultipartFile file : files) {
                
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null) 
                    throw new Exception("파일 이름이 없습니다.");                               // 예외를 던져 에러 리턴
                

                String fileExtension = StringUtils.getFilenameExtension(originalFilename);
                String newFilename   = UUID.randomUUID().toString() + "." + fileExtension;

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                Date date    = new Date();
                String today = sdf.format(date);
                    
                String folderPath = "community/" + today + "/";
                String fileUrl    = "https://"   + bucket + ".s3.amazonaws.com/" + folderPath + newFilename;
System.out.println("file url : "+ fileUrl);
                metadata = new ObjectMetadata();
                metadata.setContentType(file.getContentType());
                metadata.setContentLength(file.getSize());
                amazonS3.putObject(bucket, folderPath + newFilename, file.getInputStream(), metadata);
                
                uptImgList.add(fileUrl);
            }
            
            post.setImageUrls(uptImgList);
        } else {
            
            throw new AppException(ErrorType.aws_credentials_fail, null);
        }
    }

    // 다중 파일 삭제
    /* public void fileDelete(String filePath, String fileName) {
        
        if(amazonS3 != null) {
            amazonS3.deleteObject(new DeleteObjectRequest(filePath, fileName));
        } else {
            throw new AppException(ErrorType.aws_credentials_fail, null);
        }
    } */

    public void deleteImageFromS3(String imageUrl) {
        
        String key  = extractKeyFromUrl(imageUrl);
        String bucketName = extractBucketNameFromUrl(imageUrl);
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, key);
        amazonS3.deleteObject(deleteObjectRequest);
    }

    private String extractKeyFromUrl(String imageUrl) {
        
        // S3 URL에서 key 추출 로직 (예: https://bucket-name.s3.region.amazonaws.com/key)
        // 실제 URL 형식에 맞게 수정해야 합니다.
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }

    private String extractBucketNameFromUrl(String imageUrl) {
        
        String fullPath = null;

        // S3 URL에서 버킷 이름 추출 로직
        String bucketName = null;
        String folderNm   = null;
        String creatInfo  = null;
    
        // URL 형식에 따라 버킷 이름을 추출
        if (imageUrl.startsWith("https://") && imageUrl.contains(".s3.amazonaws.com/")) {
            
            // 형식: https://<bucket-name>.s3.amazonaws.com/<key>
            bucketName = imageUrl.split("/")[2].split("\\.")[0];
            folderNm   = imageUrl.split("/")[3];
            creatInfo  = imageUrl.split("/")[4];
        } else if (imageUrl.startsWith("https://s3.amazonaws.com/")) {
            
            // 형식: https://s3.amazonaws.com/<bucket-name>/<key>
            bucketName = imageUrl.split("/")[3];
            folderNm   = imageUrl.split("/")[4];
            creatInfo  = imageUrl.split("/")[5];
        }
        
        fullPath = bucketName + "/" + folderNm + "/" + creatInfo;
        return fullPath;
    }
}
