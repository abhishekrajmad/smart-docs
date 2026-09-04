package com.madocde.smartdocs.service;

import com.madocde.smartdocs.dto.DocumentResponse;
import com.madocde.smartdocs.entity.Document;
import com.madocde.smartdocs.entity.DocumentStatus;
import com.madocde.smartdocs.repository.DocumentChunkRepository;
import com.madocde.smartdocs.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private final DocumentRepository documentRepository;
    private final PdfTextExtractor pdfTextExtractor;
    private final TextChunker textChunker;
    private final GeminiEmbeddingService geminiEmbeddingService;
    private final DocumentChunkService documentChunkService;

    public DocumentService(DocumentRepository documentRepository,
                           PdfTextExtractor pdfTextExtractor,
                           TextChunker textChunker,
                           DocumentChunkRepository documentChunkRepository,
                           GeminiEmbeddingService geminiEmbeddingService, DocumentChunkService documentChunkService) {
        this.documentRepository = documentRepository;
        this.pdfTextExtractor = pdfTextExtractor;
        this.textChunker = textChunker;
        this.geminiEmbeddingService = geminiEmbeddingService;
        this.documentChunkService = documentChunkService;
    }

    @Transactional
    public Document uploadDocument(MultipartFile file)
        throws IOException {

        validateFile(file);

        String extractedText = pdfTextExtractor.extractText(file);

        if(extractedText.isBlank()){
            throw new IllegalArgumentException("PDF does not contain text/data");
        }

        List<String> chunks = textChunker.chunk(extractedText);
        System.out.println("Extracted characters; " + extractedText.length());
        System.out.println("Generated chunks: " + chunks.size());

        Document document = new Document();

        document.setFileName(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setStatus(DocumentStatus.UPLOADED);
        document.setCreatedAt(OffsetDateTime.now());

        Document savedDocument = documentRepository.save(document);

        processChunks(savedDocument, chunks);

        return savedDocument;
    }

    private void validateFile(MultipartFile file){
        if(file == null || file.isEmpty()){
            throw new IllegalArgumentException("File cannot be empty");
        }

        if(file.getSize() > MAX_FILE_SIZE){
            throw new IllegalArgumentException(
                    "File size cannot exceed 10 MB"
            );
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")){
            throw new IllegalArgumentException("Only PDF files are supported");
        }
    }

    private void processChunks(Document document, List<String> chunks) {
        final int batchSize = 20;

        for(int start = 0; start < chunks.size(); start += batchSize){
            int end = Math.min(start + batchSize, chunks.size());
            List<String> batch =  chunks.subList(start, end);
            System.out.println("Processing chunks: " + start + " to " + (end-1));

            List<List<Float>> embeddings = geminiEmbeddingService.generateEmbeddings(batch);
            System.out.println("Generated Embeddings: " + embeddings.size());

            documentChunkService.saveChunks(document, batch, embeddings, start);
        }
    }

    public DocumentResponse toResponse(Document document){

        return new DocumentResponse(
                document.getId(),
                document.getFileName(),
                document.getContentType(),
                document.getFileSize(),
                document.getStatus(),
                document.getCreatedAt()
        );
    }
}
