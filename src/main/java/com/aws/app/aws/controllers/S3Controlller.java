package com.aws.app.aws.controllers;

import com.aws.app.aws.services.IS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class S3Controlller {

    @Autowired
    private IS3Service s3Service;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return s3Service.uploadFile(file);
    }

    @GetMapping("/download/{file}")
    public String downloadFile(@PathVariable("file") String file) throws IOException {
        return s3Service.downloadFile(file);
    }

    @GetMapping("/listFiles")
    public List<String>  downloadFiles() throws IOException {
        return s3Service.listFiles();
    }
    @DeleteMapping("/deleteFile/{file}")
    public String  delete(@PathVariable("file") String file) {
        return s3Service.deleteFile(file);
    }
    @PutMapping("/updateFileName/{file}/{newFile}")
    public String  renameFile(@PathVariable("file") String file, @PathVariable("newFile") String newFile) {
        return s3Service.renameFile(file,newFile);
    }


}
