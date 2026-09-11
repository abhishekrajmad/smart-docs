package com.madocde.smartdocs.repository;

public interface SimilaritySearchResult {
    Long getId();
    Integer getChunkIndex();
    String getContent();
    Double getSimilarity();
}
