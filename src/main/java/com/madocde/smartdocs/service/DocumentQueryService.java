package com.madocde.smartdocs.service;

import com.madocde.smartdocs.repository.DocumentChunkRepository;
import com.madocde.smartdocs.repository.DocumentRepository;
import com.madocde.smartdocs.repository.SimilaritySearchResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentQueryService {
    private final GeminiEmbeddingService geminiEmbeddingService;
    private final DocumentChunkRepository documentChunkRepository;
    private final DocumentRepository documentRepository;

    public DocumentQueryService(
            GeminiEmbeddingService geminiEmbeddingService,
            DocumentChunkRepository documentChunkRepository,
            DocumentRepository documentRepository) {
        this.geminiEmbeddingService = geminiEmbeddingService;
        this.documentChunkRepository = documentChunkRepository;
        this.documentRepository = documentRepository;
    }

    public List<SimilaritySearchResult> search(
            Long documentId, String question, int limit) {

        documentRepository.findById(documentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Document with id: " + documentId + " not found."));

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question cannot be empty");
        }

        List<Float> queryEmbedding = geminiEmbeddingService.generateQueryEmbedding(question);

        String queryVector = toVectorString(queryEmbedding);

        return documentChunkRepository.findSimilarChunks(documentId, queryVector, limit);
    }

    private String toVectorString(List<Float> embedding) {
        StringBuilder vector = new StringBuilder("[");
        for (int i = 0; i < embedding.size(); i++) {
            if (i > 0) {
                vector.append(", ");
            }
            vector.append(embedding.get(i));
        }
        vector.append("]");

        return vector.toString();
    }
}
