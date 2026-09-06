package com.madocde.smartdocs.dto;

public class RetrievedChunkResponse {

    private Integer chunkIndex;
    private String content;

    public RetrievedChunkResponse() {}

    public RetrievedChunkResponse(Integer chunkIndex, String content) {
        this.chunkIndex = chunkIndex;
        this.content = content;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
