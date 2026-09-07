package com.madocde.smartdocs.controller;

import com.madocde.smartdocs.dto.DocumentQueryRequest;
import com.madocde.smartdocs.dto.DocumentResponse;
import com.madocde.smartdocs.dto.RetrievedChunkResponse;
import com.madocde.smartdocs.entity.Document;
import com.madocde.smartdocs.entity.DocumentChunk;
import com.madocde.smartdocs.service.DocumentQueryService;
import com.madocde.smartdocs.service.DocumentService;
import com.madocde.smartdocs.service.GeminiAnswerService;
import com.madocde.smartdocs.service.RagContextService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {
    private final DocumentService documentService;
    private final DocumentQueryService documentQueryService;
    private final RagContextService ragContextService;
    private final GeminiAnswerService geminiAnswerService;

    public DocumentController(DocumentService documentService, DocumentQueryService documentQueryService
    , RagContextService ragContextService, GeminiAnswerService geminiAnswerService) {
        this.documentService = documentService;
        this.documentQueryService = documentQueryService;
        this.ragContextService = ragContextService;
        this.geminiAnswerService = geminiAnswerService;
    }

    @PostMapping
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file) throws Exception{

        Document document = documentService.uploadDocument(file);

        DocumentResponse response = documentService.toResponse(document);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/{documentId}/query")
    public ResponseEntity<?> queryDocument(
            @PathVariable Long documentId,
            @RequestBody DocumentQueryRequest request) {
        List<DocumentChunk> chunks = documentQueryService.search(documentId, request.getQuestion(), 5);

        String context = ragContextService.buildContext(chunks);

        String answer = geminiAnswerService.generateAnswer(request.getQuestion(), context);

        return ResponseEntity.ok(answer);
    }
}
