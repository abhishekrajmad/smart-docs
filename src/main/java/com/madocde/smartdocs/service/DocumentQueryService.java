package com.madocde.smartdocs.service;

import com.madocde.smartdocs.exception.ResourceNotFoundException;
import com.madocde.smartdocs.repository.DocumentChunkRepository;
import com.madocde.smartdocs.repository.DocumentRepository;
import com.madocde.smartdocs.repository.SimilaritySearchResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentQueryService {

    @Value("${rag.retrieval.top-k}")
    private int defaultTopK;

    @Value("${rag.retrieval.min-similarity}")
    private double minSimilarity;

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
            Long documentId, String question) {

        documentRepository.findById(documentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document with id: " + documentId + " not found."));

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question cannot be empty");
        }

        List<Float> queryEmbedding = geminiEmbeddingService.generateQueryEmbedding(question);

        String queryVector = toVectorString(queryEmbedding);

//        return documentChunkRepository.findSimilarChunks(documentId, queryVector, defaultTopK);
        List<SimilaritySearchResult> results =
                documentChunkRepository.findSimilarChunks(
                        documentId,
                        queryVector,
                        defaultTopK
                );

        results = results.stream()
                .filter(result -> result.getSimilarity() >= minSimilarity)
                .toList();

        return results;
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
