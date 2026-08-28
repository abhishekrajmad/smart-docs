package com.madocde.smartdocs.repository;

import com.madocde.smartdocs.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
