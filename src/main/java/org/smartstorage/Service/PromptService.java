package org.smartstorage.Service;

import java.util.List;

public interface PromptService {

    String askGeminai(String contnet);

    String extractText(List<String[]> contnet);
}
