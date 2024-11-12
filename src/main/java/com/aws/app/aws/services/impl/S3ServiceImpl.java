package com.aws.app.aws.services.impl;


import com.aws.app.aws.services.IS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3ServiceImpl implements IS3Service {

    @Value("${upload.s3.localpath}")
    private String localPath;

    private final S3Client s3Client;

    @Autowired
    public S3ServiceImpl(S3Client s3Client){
        this.s3Client= s3Client;
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException{
        try {
            String fileName = file.getOriginalFilename();
            PutObjectRequest putObjectRequest= PutObjectRequest.builder().bucket("boucket-test").key(fileName).build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));


            return"File loaded OK";

        }catch (IOException e){throw new IOException(e.getMessage());}

    }

    @Override
    public String downloadFile(String fileName) throws IOException {
        if(!doesObjectExists(fileName)){
            return "The file does not exist!";
        }
            GetObjectRequest request = GetObjectRequest.builder().bucket("boucket-test").key(fileName).build();

        ResponseInputStream<GetObjectResponse>  result = s3Client.getObject(request);
            try (FileOutputStream fileOutputStream = new FileOutputStream(localPath + fileName)){
                byte[] read_buf = new byte[1024];
                int read_len = 0;

                while((read_len = result.read(read_buf)) > 0 ){
                    fileOutputStream.write(read_buf,0, read_len);
                }


            } catch (IOException e) {
                throw new IOException(e.getMessage());
            }
            return "File downloaded successfully";
        }

        @Override
        public List<String> listFiles() throws IOException {

            try{

                ListObjectsRequest request = ListObjectsRequest.builder().bucket("boucket-test").build();
                List<S3Object> objects = s3Client.listObjects(request).contents();
                List<String> listObjects = new ArrayList<String>();

                for (S3Object object: objects){
                    listObjects.add(object.key());

                }
                return listObjects;
            }catch(S3Exception e){
                throw new IOException(e.getMessage());
            }
        }

        @Override
        public String deleteFile(String fileName){

            if(!doesObjectExists(fileName)){
                return "The file does not exist!";
            }
            try{

                DeleteObjectRequest deleteObjectrequest = DeleteObjectRequest.builder().bucket("boucket-test").key(fileName).build();
                s3Client.deleteObject(deleteObjectrequest);
            }catch(S3Exception e){
                new IOException(e.getMessage());
            }
        return "File "+fileName+ " deleted !";
        }
    @Override
    public String renameFile(String fileName, String newFileName){

        if(!doesObjectExists(fileName)){
            return "The file does not exist!";
        }
        try{
         CopyObjectRequest copyObjectRequest= CopyObjectRequest.builder().destinationBucket("boucket-test")
                 .copySource("boucket-test/"+fileName)
                 .destinationKey(newFileName)
                 .build();

         s3Client.copyObject(copyObjectRequest);
         deleteFile(fileName);
        }catch(S3Exception e){
            new IOException(e.getMessage());
        }
        return "File "+ fileName + " updated to " + newFileName;
    }


    private boolean doesObjectExists(String objectKey){
        try{
            HeadObjectRequest hadObjectRequest =  HeadObjectRequest.builder().bucket("boucket-test").key(objectKey).build();
            s3Client.headObject(hadObjectRequest);
        }catch(S3Exception e){
            if(e.statusCode() == 404){
                return false;
            }

        }
        return true;
    }

}
