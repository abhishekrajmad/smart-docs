package com.madocde.smartdocs.controller;

import com.madocde.smartdocs.dto.DocumentResponse;
import com.madocde.smartdocs.entity.Document;
import com.madocde.smartdocs.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;

@RestController
@RequestMapping("/documents")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
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
}
