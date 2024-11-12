package com.aws.app.aws.services;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IS3Service {

    String uploadFile(MultipartFile file) throws IOException ;

    String downloadFile(String file) throws IOException;

    List<String> listFiles() throws IOException;

    String deleteFile(String fileName);

    String renameFile(String fileName, String newFileName);





}
