package org.smartstorage.Service;


import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Service
public class MultiTurnMultimodal implements MultiMediaDataProcessing{
    private static final Map<String, String> mimeTypes = new HashMap<>();

    static {
        // Example loading from a data structure (replace with your actual source)
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("bmp", "image/bmp");
        mimeTypes.put("tiff", "image/tiff");

        mimeTypes.put("mp4", "video/mp4");
        mimeTypes.put("mov", "video/quicktime");
        mimeTypes.put("avi", "video/x-msvideo");
        mimeTypes.put("wmv", "video/x-ms-wmv");

        mimeTypes.put("mp3", "audio/mpeg");
        mimeTypes.put("wav", "audio/wav");
        mimeTypes.put("ogg", "audio/ogg");

        mimeTypes.put("pdf", "application/pdf");
        mimeTypes.put("doc", "application/msword");
        mimeTypes.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        mimeTypes.put("txt", "text/plain");
        mimeTypes.put("csv", "text/csv");
        mimeTypes.put("xls", "application/vnd.ms-excel");
        mimeTypes.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        mimeTypes.put("ppt", "application/vnd.ms-powerpoint");
        mimeTypes.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        mimeTypes.put("java", "text/x-java-source");
        mimeTypes.put("py", "text/x-python");
        mimeTypes.put("js", "text/javascript");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("html", "text/html");
        mimeTypes.put("php", "text/x-php");
        mimeTypes.put("c", "text/x-c");
        mimeTypes.put("cpp", "text/x-c++");
        mimeTypes.put("go", "text/x-go");
        mimeTypes.put("rb", "text/x-ruby");
    }


    public  String getSummaryForMedia(String filepath) throws IOException {
        GenerateContentResponse response = null;
        try (VertexAI vertexAi = new VertexAI("build-and-blog-marathon", "us-central1"); ) {
            GenerationConfig generationConfig =
                    GenerationConfig.newBuilder()
                            .setMaxOutputTokens(8192)
                            .setTemperature(1F)
                            .setTopP(0.95F)
                            .build();
//            List<SafetySetting> safetySettings = Arrays.asList(
//                    SafetySetting.newBuilder()
//                            .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
//                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_ONLY_HIGH)
//                            .build(),
//                    SafetySetting.newBuilder()
//                            .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
//                            .setThreshold(SafetySetting.HarmBlockThreshold.UNRECOGNIZED)
//                            .build(),
//                    SafetySetting.newBuilder()
//                            .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
//                            .setThreshold(SafetySetting.HarmBlockThreshold.UNRECOGNIZED)
//                            .build(),
//                    SafetySetting.newBuilder()
//                            .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
//                            .setThreshold(SafetySetting.HarmBlockThreshold.UNRECOGNIZED)
//                            .build()
//            );
            GenerativeModel model =
                    new GenerativeModel.Builder()
                            .setModelName("gemini-1.5-flash-002")
                            .setVertexAi(vertexAi)
                            .setGenerationConfig(generationConfig)
//                            .setSafetySettings(safetySettings)
                            .build();

            File image1_1File = new File(filepath);
            byte[] image1_1Bytes = new byte[(int) image1_1File.length()];
            try(FileInputStream image1_1FileInputStream = new FileInputStream(image1_1File)) {
                image1_1FileInputStream.read(image1_1Bytes);
            }

            // check if the file path is present in the dataset
            int dotIndex = filepath.lastIndexOf('.');
            if (dotIndex == -1) {
                return "No Extension Found";
            }

            String extension = filepath.substring(dotIndex + 1).toLowerCase();
            String mimeType = mimeTypes.get(extension);
            mimeType = mimeType != null ? mimeType : "application/octet-stream";
//            mimeType =  "application/octet-stream";
            System.out.println(mimeType);
            var image1_1 = PartMaker.fromMimeTypeAndData(
                    mimeType, image1_1Bytes);
            // For multi-turn responses, start a chat session.
            ChatSession chatSession = model.startChat();
            response = chatSession.sendMessage(ContentMaker.fromMultiModalData(image1_1, "give a short brief in 10 words"));

        }catch (Exception e ){
            e.printStackTrace();
        }
        return ResponseHandler.getText(response);
    }
}