package com.madocde.smartdocs.service;

import com.madocde.smartdocs.dto.DocumentResponse;
import com.madocde.smartdocs.entity.Document;
import com.madocde.smartdocs.entity.DocumentStatus;
import com.madocde.smartdocs.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;

@Service
public class DocumentService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Document uploadDocument(MultipartFile file)
        throws IOException {

        validateFile(file);

        Document document = new Document();

        document.setFileName(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setStatus(DocumentStatus.UPLOADED);
        document.setCreatedAt(OffsetDateTime.now());

        return documentRepository.save(document);
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
