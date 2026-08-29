package com.madocde.smartdocs.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TextChunkerTest {

    private final TextChunker textChunker = new TextChunker();

    @Test
    void shouldCreateChunks(){

        String text = "A".repeat(2500);
        List<String> chunks = textChunker.chunk(text);

        assertEquals(3, chunks.size());

        assertEquals(1000, chunks.get(0).length());
        assertEquals(1000, chunks.get(1).length());
        assertEquals(900, chunks.get(2).length());
    }

    @Test
    void shouldReturnEmptyListForEmptyText() {
        List<String> chunks = textChunker.chunk("");
        assertTrue(chunks.isEmpty());
    }
}
