package org.smartstorage.Utility;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartstorage.Entity.MetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class DataBucketUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBucketUtil.class);

    private static final String FOLDER_MIME_TYPE = "application/x-www-form-urlencoded";

    @Value("${gcp.config.file}")
    private String gcpConfigFile;

    @Value("${gcp.project.id}")
    private String gcpProjectId;

    @Value("${gcp.bucket.id}")
    private String gcpBucketId;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    public Bucket getTheGCPBucket() throws IOException {
        InputStream inputStream = new ClassPathResource(gcpConfigFile).getInputStream();
        StorageOptions options = StorageOptions.newBuilder().setProjectId(gcpProjectId)
                .setCredentials(GoogleCredentials.fromStream(inputStream)).build();
        Storage storage = options.getService();
        Bucket bucket = storage.get(gcpBucketId,Storage.BucketGetOption.fields());
        return bucket;
    }

    public Long uploadFileForBoardAssets(String parentFolder, String uniqueIdentifier,MultipartFile files,String filePath) throws IOException {
        Long count = 0L;
        try{
            Bucket bucket = getTheGCPBucket();
            if (!StringUtils.isEmpty(parentFolder)) {
                Blob parentFolderBlob = bucket.get(parentFolder + "/");
                if (parentFolderBlob == null) {
                    Blob blob = bucket.create(parentFolder + "/", new byte[0], FOLDER_MIME_TYPE);
                    log.info("parentFolder created "+blob.getBucket()+" "+blob.getName());
                }
                String fileName = files.getOriginalFilename();
                if (!StringUtils.isEmpty(fileName)) {
                    byte[] bytes = files.getBytes();
                    Blob fileBlob = bucket.create(uniqueIdentifier + fileName, bytes, "text/pdf");
                    log.info("file uploaded "+fileBlob.getBucket()+" "+fileBlob.getName());
                }
            }

            if(filePath!=null) {
                Path path = Paths.get(filePath);
                count = Files.lines(path).count();
                Files.delete(path);
            }

        }
        catch (Exception e){
            log.error("Exception occurred ", e.getStackTrace());
        }
        return count;
    }



    public void getCSVFileFromTheGCP(File filePathToWrite,String filePath) throws IOException {
        log.info("fetching blob content..");
        Bucket bucket = getTheGCPBucket();
        log.info("file path  found this " + filePath);
        Blob blob = bucket.get(filePath);
        log.info("printing blob "+ blob);
        try {
            byte[] content = blob.getContent();
            OutputStream os = new FileOutputStream(filePathToWrite);
            os.write(content);
            os.flush();
            os.close();
        } catch (Exception e) {
            log.info("No content found: " + e);
        }
    }
}
