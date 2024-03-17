package com.multicloudpoc.awspoc.services;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private final AmazonS3 s3Client;
    private final String bucketName;




    public S3Service(@Value("${bucket.name}") String bucketName, @Value("${aws.accessKey}")String accesskey, @Value("${aws.secretKey}") String secretKey) {
        AWSCredentials credentials = new BasicAWSCredentials(accesskey, secretKey);
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
        this.bucketName = bucketName;
        this.s3Client = AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).withRegion(Regions.AP_SOUTH_1).build();
    }

    // Upload PDF file to S3 bucket
    public void uploadPDF(MultipartFile file) throws IOException {
        s3Client.putObject(new PutObjectRequest(bucketName, file.getOriginalFilename(), file.getInputStream(), new ObjectMetadata()));
    }

    // Download PDF file from S3 bucket
    public byte[] downloadPDF(String filename) {
        S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, filename));
        S3ObjectInputStream inputStream = object.getObjectContent();
        try {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // List all PDF files in S3 bucket
    public List<String> listPDFs() {
        ListObjectsV2Result result = s3Client.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        return objects.stream()
                .map(S3ObjectSummary::getKey)
                .filter(key -> key.endsWith(".pdf"))
                .collect(Collectors.toList());
    }

    // Generate presigned URL for accessing a file in S3 bucket
    public String generatePresignedUrl(String filename) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, filename);
        return s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    // Delete PDF file from S3 bucket
    public boolean deletePDF(String filename) {
        try {
            s3Client.deleteObject(bucketName, filename);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

