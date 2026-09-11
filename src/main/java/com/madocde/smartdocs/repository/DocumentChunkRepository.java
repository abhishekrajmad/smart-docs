package com.madocde.smartdocs.repository;

import com.madocde.smartdocs.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {

    @Query(value = """
            SELECT
                id,
                chunk_index,
                content,
                1 - (embedding <=> CAST(:queryVector AS vector)) AS similarity
            FROM document_chunks
            WHERE document_id = :documentId
            ORDER BY embedding <=> CAST(:queryVector AS vector)
            LIMIT :limit
            """, nativeQuery = true)
    List<SimilaritySearchResult> findSimilarChunks(
            @Param("documentId") Long documentId,
            @Param("queryVector") String queryVector,
            @Param("limit") int limit
    );
}
