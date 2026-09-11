package com.madocde.smartdocs.service;

import com.madocde.smartdocs.entity.Document;
import com.madocde.smartdocs.entity.DocumentChunk;
import com.madocde.smartdocs.repository.DocumentChunkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentChunkService {
    private final DocumentChunkRepository documentChunkRepository;

    public DocumentChunkService(DocumentChunkRepository documentChunkRepository) {
        this.documentChunkRepository = documentChunkRepository;
    }

    @Transactional
    public void saveChunks(Document document, List<String> chunks,
                            List<List<Float>> embeddings, int startIndex) {
        if(chunks.size() != embeddings.size()) {
            throw new IllegalArgumentException("Chunks count and embeddings count do not match");
        }
        List<DocumentChunk> documentChunks = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            DocumentChunk chunk = new DocumentChunk();

            chunk.setDocument(document);
            chunk.setChunkIndex(startIndex + i);
            chunk.setContent(chunks.get(i));
            chunk.setCreatedAt(OffsetDateTime.now());

            List<Float> embedding = embeddings.get(i);

            float[] vector = new float[embedding.size()];

            for (int j = 0; j < embedding.size(); j++) {
                vector[j] = embedding.get(j);
            }

            chunk.setEmbedding(vector);

            documentChunks.add(chunk);
        }

        documentChunkRepository.saveAll(documentChunks);
    }

}
