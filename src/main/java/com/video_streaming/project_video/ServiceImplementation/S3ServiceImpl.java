package com.video_streaming.project_video.ServiceImplementation;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.video_streaming.project_video.Service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3ServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public String uploadFile(File file) {
        if (file == null || !file.exists()) {
            return "Error: File does not exist - " + (file != null ? file.getAbsolutePath() : "null");
        }

        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, file.getName(), file);
            amazonS3.putObject(request);
            return "File uploaded successfully: " + file.getName() + "URL: " + amazonS3.getUrl(bucketName, file.getName());
        } catch (AmazonServiceException ase) {
            // AWS service error (e.g., permission issues, bucket not found)
            ase.printStackTrace();
            return "Service error uploading file: " + ase.getMessage();
        } catch (SdkClientException sce) {
            // SDK/client-side issue (e.g., file not found, network error)
            sce.printStackTrace();
            return "Client error uploading file: " + sce.getMessage();
        } catch (Exception e) {
            // Any other unexpected error
            e.printStackTrace();
            return "Unexpected error uploading file: " + e.getMessage();
        }
    }


    @Override
    public S3Object downloadFile(String fileName) {
        return amazonS3.getObject(bucketName, fileName);
    }
}
