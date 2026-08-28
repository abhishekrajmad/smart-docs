package com.madocde.smartdocs.dto;

import com.madocde.smartdocs.entity.DocumentStatus;

import java.time.OffsetDateTime;

public class DocumentResponse {
    private Long id;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private DocumentStatus status;
    private OffsetDateTime createdAt;

    public DocumentResponse() {}

    public DocumentResponse(Long id, String fileName,
                            String contentType, Long fileSize,
                            DocumentStatus status,
                            OffsetDateTime createdAt) {
        this.id = id;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
