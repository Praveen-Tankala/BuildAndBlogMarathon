package org.smartstorage.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PromptServiceImpl implements PromptService{


    private static final String GEMINI_MODEL = "gemini-1.5-flash";

    private static final String API_KEY ="AIzaSyBfTwZGghdLwLO6LNPEYgmZ_mqoAi28-JI";
    private static String conversationHistory = "";


    @Override
    public  String askGeminai(String prompt) {

        String fullPrompt = prompt;

        if (!Strings.isBlank(conversationHistory)) {
            fullPrompt = "[Context]" + conversationHistory + " [Content] " + prompt + "/n As a smart observer you have to provide the short brief about the provides data with not exceeding 50 words";
        }

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Prepare request entity
        fullPrompt = getPromptBody(fullPrompt);
        HttpEntity<String> requestEntity = new HttpEntity<>(fullPrompt, headers);


        // Perform HTTP POST request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL + ":generateContent?key="+ API_KEY,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        HttpStatus statusCode = responseEntity.getStatusCode();

        // Handle the response based on the status code
        if (statusCode == HttpStatus.OK) {
            String responseText = responseEntity.getBody();
            try {
                responseText = parseGeminiResponse(responseText);
                conversationHistory += prompt + "\n" + responseText + "\n"; // Update conversation history
            } catch (Exception e) {
                log.error("Error in Parding");
            }
            return responseText; // Return the fetched summary response
        } else {
            throw new RuntimeException("API request failed with status code: " + statusCode + " and response: " + responseEntity.getBody());
        }
    }

    public static String getPromptBody(String prompt) {
        // Create prompt for generating summary in document language
        JSONObject promptJson = new JSONObject();

        // Array to contain all the content-related data, including the text and role
        JSONArray contentsArray = new JSONArray();
        JSONObject contentsObject = new JSONObject();
        contentsObject.put("role", "user");

        // Array to hold the specific parts (or sections) of the user's input text
        JSONArray partsArray = new JSONArray();
        JSONObject partsObject = new JSONObject();
        partsObject.put("text", prompt);
        partsArray.add(partsObject);
        contentsObject.put("parts", partsArray);

        contentsArray.add(contentsObject);
        promptJson.put("contents", contentsArray);

        // Array to hold various safety setting objects to ensure the content is safe and appropriate
        JSONArray safetySettingsArray = new JSONArray();

        // Adding safety settings for hate speech
        JSONObject hateSpeechSetting = new JSONObject();
        hateSpeechSetting.put("category", "HARM_CATEGORY_HATE_SPEECH");
        hateSpeechSetting.put("threshold", "BLOCK_ONLY_HIGH");
        safetySettingsArray.add(hateSpeechSetting);

        // Adding safety settings for dangerous content
        JSONObject dangerousContentSetting = new JSONObject();
        dangerousContentSetting.put("category", "HARM_CATEGORY_DANGEROUS_CONTENT");
        dangerousContentSetting.put("threshold", "BLOCK_ONLY_HIGH");
        safetySettingsArray.add(dangerousContentSetting);

        // Adding safety settings for sexually explicit content
        JSONObject sexuallyExplicitSetting = new JSONObject();
        sexuallyExplicitSetting.put("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT");
        sexuallyExplicitSetting.put("threshold", "BLOCK_ONLY_HIGH");
        safetySettingsArray.add(sexuallyExplicitSetting);

        // Adding safety settings for harassment content
        JSONObject harassmentSetting = new JSONObject();
        harassmentSetting.put("category", "HARM_CATEGORY_HARASSMENT");
        harassmentSetting.put("threshold", "BLOCK_ONLY_HIGH");
        safetySettingsArray.add(harassmentSetting);

        promptJson.put("safetySettings", safetySettingsArray);

        // Creating and setting generation configuration parameters such as temperature and topP
        JSONObject parametersJson = new JSONObject();
        parametersJson.put("temperature", 0.5);
        parametersJson.put("topP", 0.99);
        promptJson.put("generationConfig", parametersJson);

        // Convert the JSON object to a JSON string
        return promptJson.toJSONString();
    }

    public static String parseGeminiResponse(String jsonResponse) throws IOException, ParseException {
        // Parse the JSON string
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonResponse);

        // Get the "candidates" array
        JSONArray candidatesArray = (JSONArray) jsonObject.get("candidates");

        // Assuming there's only one candidate (index 0), extract its content
        JSONObject candidateObject = (JSONObject) candidatesArray.get(0);
        JSONObject contentObject = (JSONObject) candidateObject.get("content");

        // Get the "parts" array within the content
        JSONArray partsArray = (JSONArray) contentObject.get("parts");

        // Assuming there's only one part (index 0), extract its text
        JSONObject partObject = (JSONObject) partsArray.get(0);
        String responseText = (String) partObject.get("text");

        return responseText;
    }

    public String extractText(List<String[]> content) {
        // Use Apache Tika to extract text from PDF, DOCX, or other textual files
        String promptString = content.stream()
                .flatMap(Arrays::stream) // Flatten the list of arrays into a stream of strings
                .collect(Collectors.joining(", ")); // Join each entry with a comma
        String chat = askGeminai(promptString);
        log.error("Content "+ promptString+ " "+ chat);
        return chat;
    }


}
