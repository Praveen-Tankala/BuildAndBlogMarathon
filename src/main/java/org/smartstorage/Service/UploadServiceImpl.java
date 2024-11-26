package org.smartstorage.Service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.smartstorage.Entity.MakeMultipartFile;
import org.smartstorage.Entity.MetaData;
import org.smartstorage.Utility.DataBucketUtil;
import org.smartstorage.Utility.ResponseModel;
import org.smartstorage.event.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Service
@Slf4j
public class UploadServiceImpl implements UploadService{

    public static final  String CURRENT_DIRECTORY = System.getProperty("user.dir");

    public static final  String CURRENT_FOLDER = "Downloads";


    @Autowired
    PromptService promptService;

    @Autowired
    MultiTurnMultimodal multiTurnMultimodal;


    @Autowired
    DataBucketUtil dataBucketUtil;


    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @Override
    public ResponseModel uploadDataToCloud(MultipartFile file, String userId) {
        try {
            // Convert MultipartFile to File
            String fileName = System.getProperty("user.home") +"/"+ file.getOriginalFilename();
            File convFile = new File(fileName);
            if (!convFile.createNewFile()) {
                throw new IOException("Failed to create file: " + convFile.getAbsolutePath());
            }
            file.transferTo(convFile);

            String data = "";

            if(fileName.contains(".jpeg") || fileName.contains(".jpg") || fileName.contains(".png")) {
                FileInputStream fileInputStream = new FileInputStream(convFile);
                MemoryCacheImageInputStream memoryCache = new MemoryCacheImageInputStream(fileInputStream);
                BufferedImage bufferedImage = ImageIO.read(memoryCache);
                String string = bufferedImage.toString();
                String extractedText = promptService.askGeminai(string);
                return new ResponseModel(200, "Success!", extractedText);
            }

            if(fileName.contains(".pdf")) {
                PDDocument document = PDDocument.load(convFile);
                if (!document.isEncrypted()) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    data = stripper.getText(document);
                    System.out.println("Text:" + data);
                }
                document.close();
            }

            if(fileName.contains(".csv")) {
                FileReader filereader = new FileReader(convFile);
                CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
                CSVReader csvReader = new CSVReaderBuilder(filereader)
                        .withCSVParser(parser)
                        .build();
                List<String[]> allData = csvReader.readAll();
                String extractedText = promptService.extractText(allData);
                return new ResponseModel(200, "Success!", extractedText);
            }


            if(fileName.contains(".txt")) {
                data = new String(
                        Files.readAllBytes(Paths.get(fileName)));
            }
            // Extract text using Apache Tika
            String extractedText = promptService.askGeminai(data);


            MetaData metaData = new MetaData()
                    .setDescription(extractedText)
                    .setUploadDate(String.valueOf(System.currentTimeMillis()))
                    .setStatus(1);

            MultipartFile result = getMultipartFile(fileName, file.getOriginalFilename());
            dataBucketUtil.uploadFileForBoardAssets(userId , String.valueOf(System.currentTimeMillis()), result,fileName, metaData);

            return new ResponseModel(200, "Success!", extractedText);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseModel(400, "Failed to process the file.", null);
        }
    }

    public MultipartFile getMultipartFile(String filePath, String fileName) {
        Path path = Paths.get(filePath);
        String name = fileName;
        String originalFileName = fileName;
        String contentType = "text/plain";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
        }
        MultipartFile result = new MakeMultipartFile(name,
                originalFileName, contentType, content);
        return result;
    }

    public ResponseModel postDataToCloud(MultipartFile file, String uuid) {
        String summaryForMedia = "";
        try {
            // Convert MultipartFile to File
            String fileName = System.getProperty("user.home") + "/" + file.getOriginalFilename();
            File convFile = new File(fileName);
            if (!convFile.createNewFile()) {
                throw new IOException("Failed to create file: " + convFile.getAbsolutePath());
            }
            file.transferTo(convFile);

            summaryForMedia = multiTurnMultimodal.getSummaryForMedia(fileName);
            MetaData metaData = new MetaData()
                    .setName(file.getOriginalFilename())
                    .setDescription(summaryForMedia)
                    .setUploadDate(String.valueOf(System.currentTimeMillis()))
                    .setLink("")
                    .setStatus(1);

            MultipartFile result = getMultipartFile(fileName, file.getOriginalFilename());
            dataBucketUtil.uploadFileForBoardAssets(uuid , String.valueOf(System.currentTimeMillis()), result,fileName, metaData);

            return new ResponseModel(200, "Success!", summaryForMedia);

        }catch (Exception e){
            log.error(e.getLocalizedMessage());
        }
        return new ResponseModel(204, "No Content!", summaryForMedia);
    }




}
