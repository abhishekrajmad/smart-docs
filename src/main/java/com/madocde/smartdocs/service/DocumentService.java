package com.madocde.smartdocs.service;

import com.madocde.smartdocs.dto.DocumentResponse;
import com.madocde.smartdocs.entity.Document;
import com.madocde.smartdocs.entity.DocumentChunk;
import com.madocde.smartdocs.entity.DocumentStatus;
import com.madocde.smartdocs.repository.DocumentChunkRepository;
import com.madocde.smartdocs.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final DocumentChunkRepository documentChunkRepository;
    private final GeminiEmbeddingService geminiEmbeddingService;

    public DocumentService(DocumentRepository documentRepository,
                           PdfTextExtractor pdfTextExtractor,
                           TextChunker textChunker,
                           DocumentChunkRepository documentChunkRepository,
                           GeminiEmbeddingService geminiEmbeddingService) {
        this.documentRepository = documentRepository;
        this.pdfTextExtractor = pdfTextExtractor;
        this.textChunker = textChunker;
        this.documentChunkRepository = documentChunkRepository;
        this.geminiEmbeddingService = geminiEmbeddingService;
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

        List<Float> embedding = geminiEmbeddingService.generateEmbedding(chunks.get(0));
        System.out.println("Embedding dimensions: " + embedding.size());
        Document document = new Document();

        document.setFileName(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setStatus(DocumentStatus.UPLOADED);
        document.setCreatedAt(OffsetDateTime.now());

        Document savedDocument = documentRepository.save(document);

        saveChunks(savedDocument, chunks);

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

//        if(!"application/pdf".equalsIgnoreCase(file.getContentType())){
//            throw new IllegalArgumentException(
//                    "Only PDF files are supported"
//            );
//        }
    }

    private void saveChunks(Document document, List<String> chunks) {
        List<DocumentChunk> documentChunks = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            DocumentChunk chunk = new DocumentChunk();

            chunk.setDocument(document);
            chunk.setChunkIndex(i);
            chunk.setContent(chunks.get(i));
            chunk.setCreatedAt(OffsetDateTime.now());

            documentChunks.add(chunk);
        }

        documentChunkRepository.saveAll(documentChunks);
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
