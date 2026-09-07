package com.madocde.smartdocs.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class GeminiAnswerService {

    private final Client geminiClient;

    public GeminiAnswerService(Client geminiClient) {
        this.geminiClient = geminiClient;
    }

    public String generateAnswer(String question, String context) {

        String prompt = """
                You are a helpful document question-answering assistant.
                
                Answer the user's question using only the information provided
                in the document context below.

                If the answer cannot be found in the context, say:
                "I could not find the answer in the document."

                Document context:
                %s

                User question:
                %s
                """.formatted(context, question);

        GenerateContentResponse response = geminiClient.models.generateContent(
                "gemini-3.6-flash", prompt, null);

        return response.text();
    }
}
