package org.smartstorage.Service;

import org.smartstorage.Utility.ResponseModel;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {

    ResponseModel uploadDataToCloud(MultipartFile multipartFile, String userId);


}
