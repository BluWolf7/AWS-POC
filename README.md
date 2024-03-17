AWS-S3-SpringBoot POC

Step 1 clone 
Step 2 Login to aws, go to IAM create user with s3fullaccess policy, create/get accesskeys
Step 3 in aws s3 create a bucket to which pdf files can be uploaded/downloaded 
Step 4 in application.properties, add bucket name, region where bucket is created, accessKeys etc
Step 5 mvn clean 
Step 6 mvn install 
Step 7 mvn package 
Step 8 Run the app 
Step 9 Hit end points using postman (Refer controller for help)
