package com.madocde.smartdocs.service;

import com.madocde.smartdocs.entity.DocumentChunk;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagContextService {

    public String buildContext(List<DocumentChunk> chunks) {

        if(chunks == null || chunks.isEmpty()) return "";

        StringBuilder context = new StringBuilder();
        for (DocumentChunk chunk : chunks) {
            context.append("Chunk ")
                    .append(chunk.getChunkIndex())
                    .append(":\n");

            context.append(chunk.getContent())
                    .append("\n");
        }

        return context.toString();
    }
}
