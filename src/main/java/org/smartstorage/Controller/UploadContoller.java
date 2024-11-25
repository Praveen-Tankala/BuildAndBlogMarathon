package org.smartstorage.Controller;

import com.sun.istack.NotNull;
import org.smartstorage.Service.UploadService;
import org.smartstorage.Utility.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadContoller {


    @Autowired
    UploadService uploadService;


    @PostMapping("/uploadFile")
    public ResponseEntity<ResponseModel> extractTextFromFile(@RequestParam("file") MultipartFile file, @NotNull String userId) {
        ResponseModel responseModel = uploadService.uploadDataToCloud(file, userId);
        return new ResponseEntity<>(responseModel, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
