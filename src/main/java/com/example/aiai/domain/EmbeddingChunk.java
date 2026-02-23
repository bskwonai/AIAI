package com.example.aiai.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "embedding_chunks")
public class EmbeddingChunk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private KnowledgeDocument document;

    @Column(nullable = false)
    private int chunkIndex;

    @Lob
    @Column(nullable = false)
    private String chunkText;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String vectorJson;

    public Long getId() { return id; }
    public KnowledgeDocument getDocument() { return document; }
    public void setDocument(KnowledgeDocument document) { this.document = document; }
    public int getChunkIndex() { return chunkIndex; }
    public void setChunkIndex(int chunkIndex) { this.chunkIndex = chunkIndex; }
    public String getChunkText() { return chunkText; }
    public void setChunkText(String chunkText) { this.chunkText = chunkText; }
    public String getVectorJson() { return vectorJson; }
    public void setVectorJson(String vectorJson) { this.vectorJson = vectorJson; }
}
