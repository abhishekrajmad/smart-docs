package com.madocde.smartdocs.service;

import com.google.genai.Client;
import com.google.genai.types.EmbedContentConfig;
import com.google.genai.types.EmbedContentResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GeminiEmbeddingService {

    private final Client geminiClient;

    public GeminiEmbeddingService(Client geminiClient) {
        this.geminiClient = geminiClient;
    }

    public List<Float> generateEmbedding(String text){
        EmbedContentConfig config = EmbedContentConfig.builder()
                .outputDimensionality(768)
                .taskType("RETRIEVAL_DOCUMENT")
                .build();

        EmbedContentResponse response = geminiClient.models.embedContent(
                "gemini-embedding-001", text, config);

        return response.embeddings()
                .flatMap(embeddings ->
                        embeddings.isEmpty()
                            ? java.util.Optional.empty()
                            : java.util.Optional.of(embeddings.get(0))
                )
                .flatMap(embedding -> embedding.values())
                .orElse(Collections.emptyList());
    }
}
