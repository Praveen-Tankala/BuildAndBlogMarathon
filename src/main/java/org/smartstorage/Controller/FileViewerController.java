package org.smartstorage.Controller;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;
import lombok.extern.slf4j.Slf4j;
import org.smartstorage.Entity.MetaData;
import org.smartstorage.Entity.MetaReponse;
import org.smartstorage.Repository.MetaDataRepository;
import org.smartstorage.Service.UploadService;
import org.smartstorage.Utility.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class FileViewerController {


    // Simulate fetching file names from a bucket
    private final List<String> fileNames = List.of("1732533829990testAPI.txt");


    @Value("${gcp.config.file}")
    private String gcpConfigFile;

    @Value("${gcp.project.id}")
    private String gcpProjectId;

    @Value("${gcp.bucket.id}")
    private String gcpBucketId;

    @Autowired
    MetaDataRepository metaDataRepository;

    @Autowired
    UploadService uploadService;

    @GetMapping("/files")
    public String showFiles2(Model model) throws IOException {
        // Get a list of files from your GCS bucket
        List<Blob> files = getFilesFromBucket(gcpBucketId);
        List<MetaData> allFileData = metaDataRepository.findAll();
        List<MetaReponse> required = allFileData.stream().map(t -> new MetaReponse().setLogId(t.getLogId()).setDescription(t.getDescription()).setSize(t.getSize()).setUploadDate(t.getUploadDate()).setName(t.getName())).collect(Collectors.toList());
        Map<String, Blob> collect = files.stream().collect(Collectors.toMap(BlobInfo::getName, t -> t));
        for(MetaReponse metaReponse : required){
            Blob blob = collect.get(metaReponse.getName());
            metaReponse.setBlob(blob);
        }
        log.error("required "+ required);
        model.addAttribute("files", required);
        return "view";
    }


    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        uploadService.postDataToCloud(file, "hello-world");
        return "redirect:/files";
    }
    @GetMapping("/file/{fileName}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String fileName) throws IOException {
        InputStream inputStream = new ClassPathResource(gcpConfigFile).getInputStream();
        StorageOptions options = StorageOptions.newBuilder().setProjectId(gcpProjectId)
                .setCredentials(GoogleCredentials.fromStream(inputStream)).build();
        Storage storage = options.getService();
        Bucket bucket = storage.get(gcpBucketId,Storage.BucketGetOption.fields());

        Blob blob = bucket.get(fileName);
        ReadChannel readChannel = storage.reader(blob.getBlobId());
        InputStream inputStream2 = Channels.newInputStream(readChannel);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(Files.probeContentType(Paths.get(fileName))))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(new InputStreamResource(inputStream2));
    }


    public List<Blob> getFilesFromBucket(String bucketName) throws IOException {
        InputStream inputStream = new ClassPathResource(gcpConfigFile).getInputStream();
        StorageOptions options = StorageOptions.newBuilder().setProjectId(gcpProjectId)
                .setCredentials(GoogleCredentials.fromStream(inputStream)).build();
        Storage storage = options.getService();
        Bucket bucket = storage.get(gcpBucketId,Storage.BucketGetOption.fields());

        // List all blobs in the bucket
        Storage.BlobListOption option = Storage.BlobListOption.pageSize(100);
        Page<Blob> blobs = bucket.list(option);

        List<Blob> fileList = new ArrayList<>();
        while (true) {
            for (Blob blob : blobs.iterateAll()) {
                fileList.add(blob);
            }
            blobs = blobs.getNextPage();
            if (blobs == null) {
                break;
            }
        }

        return fileList;
    }

    @GetMapping("/index.html")
    public String showFiles(Model model) {
        List<FileInfo> files = new ArrayList<>();
        for (String fileName : fileNames) {
            String signedUrl = generateSignedUrl(fileName);
            System.out.println(signedUrl);
            files.add(new FileInfo(fileName, signedUrl));
        }
        model.addAttribute("files", files);
        return "index";
    }

    private String generateSignedUrl(String fileName) {
        try {
            InputStream inputStream = new ClassPathResource(gcpConfigFile).getInputStream();
            StorageOptions options = StorageOptions.newBuilder().setProjectId(gcpProjectId)
                    .setCredentials(GoogleCredentials.fromStream(inputStream)).build();
            Storage storage = options.getService();
            URL signedUrl = storage.signUrl(
                    BlobInfo.newBuilder(gcpBucketId, fileName).build(),
                    15, // URL validity duration
                    TimeUnit.MINUTES,
                    Storage.SignUrlOption.withV4Signature()
            );
            return signedUrl.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public static class FileInfo {
        private final String name;
        private final String url;

        public FileInfo(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }
}
