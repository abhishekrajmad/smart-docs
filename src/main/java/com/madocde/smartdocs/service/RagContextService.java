package com.madocde.smartdocs.service;

import com.madocde.smartdocs.repository.SimilaritySearchResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagContextService {

    public String buildContext(List<SimilaritySearchResult> chunks) {

        if(chunks == null || chunks.isEmpty()) return "";

        StringBuilder context = new StringBuilder();
        for (SimilaritySearchResult chunk : chunks) {
            context.append("Chunk ")
                    .append(chunk.getChunkIndex())
                    .append(":\n");

            context.append(chunk.getContent())
                    .append("\n\n");
        }

        return context.toString();
    }
}
