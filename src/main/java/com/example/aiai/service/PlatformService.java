package com.example.aiai.service;

import com.example.aiai.domain.*;
import com.example.aiai.dto.ChatDtos;
import com.example.aiai.dto.ChatbotDtos;
import com.example.aiai.dto.KnowledgeDtos;
import com.example.aiai.dto.WorkspaceDtos;
import com.example.aiai.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class PlatformService {

    private final WorkspaceRepository workspaceRepository;
    private final KnowledgeDocumentRepository documentRepository;
    private final EmbeddingChunkRepository embeddingChunkRepository;
    private final ChatbotRepository chatbotRepository;
    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final AiGatewayService aiGatewayService;
    private final ObjectMapper objectMapper;

    public PlatformService(WorkspaceRepository workspaceRepository,
                           KnowledgeDocumentRepository documentRepository,
                           EmbeddingChunkRepository embeddingChunkRepository,
                           ChatbotRepository chatbotRepository,
                           ChatSessionRepository sessionRepository,
                           ChatMessageRepository messageRepository,
                           AiGatewayService aiGatewayService,
                           ObjectMapper objectMapper) {
        this.workspaceRepository = workspaceRepository;
        this.documentRepository = documentRepository;
        this.embeddingChunkRepository = embeddingChunkRepository;
        this.chatbotRepository = chatbotRepository;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.aiGatewayService = aiGatewayService;
        this.objectMapper = objectMapper;
    }

    public WorkspaceDtos.WorkspaceResponse createWorkspace(String userId, WorkspaceDtos.CreateWorkspaceRequest request) {
        Workspace workspace = new Workspace();
        workspace.setName(request.name());
        workspace.setOwnerId(userId);
        Workspace saved = workspaceRepository.save(workspace);
        return new WorkspaceDtos.WorkspaceResponse(saved.getId(), saved.getName(), saved.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<WorkspaceDtos.WorkspaceResponse> listWorkspaces(String userId) {
        return workspaceRepository.findByOwnerId(userId).stream()
                .map(w -> new WorkspaceDtos.WorkspaceResponse(w.getId(), w.getName(), w.getCreatedAt()))
                .toList();
    }

    public KnowledgeDtos.DocumentResponse addDocument(String userId, Long workspaceId, KnowledgeDtos.CreateDocumentRequest request) {
        Workspace workspace = ownedWorkspace(userId, workspaceId);
        KnowledgeDocument document = new KnowledgeDocument();
        document.setWorkspace(workspace);
        document.setTitle(request.title());
        document.setContent(request.content());
        KnowledgeDocument saved = documentRepository.save(document);

        embeddingChunkRepository.deleteByDocumentId(saved.getId());
        List<String> chunks = splitChunks(saved.getContent(), 500);
        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            List<Double> embedding = aiGatewayService.embed(chunk);
            EmbeddingChunk embeddingChunk = new EmbeddingChunk();
            embeddingChunk.setDocument(saved);
            embeddingChunk.setChunkIndex(i);
            embeddingChunk.setChunkText(chunk);
            embeddingChunk.setVectorJson(writeVector(embedding));
            embeddingChunkRepository.save(embeddingChunk);
        }

        return new KnowledgeDtos.DocumentResponse(saved.getId(), workspaceId, saved.getTitle(), saved.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<KnowledgeDtos.DocumentResponse> listDocuments(String userId, Long workspaceId) {
        ownedWorkspace(userId, workspaceId);
        return documentRepository.findByWorkspaceId(workspaceId).stream()
                .map(d -> new KnowledgeDtos.DocumentResponse(d.getId(), workspaceId, d.getTitle(), d.getCreatedAt()))
                .toList();
    }

    public ChatbotDtos.ChatbotResponse createChatbot(String userId, Long workspaceId, ChatbotDtos.CreateChatbotRequest request) {
        Workspace workspace = ownedWorkspace(userId, workspaceId);
        Chatbot chatbot = new Chatbot();
        chatbot.setWorkspace(workspace);
        chatbot.setName(request.name());
        chatbot.setModel(request.model());
        chatbot.setSystemPrompt(request.systemPrompt());
        Chatbot saved = chatbotRepository.save(chatbot);

        return new ChatbotDtos.ChatbotResponse(saved.getId(), workspaceId, saved.getName(), saved.getModel(), saved.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<ChatbotDtos.ChatbotResponse> listChatbots(String userId, Long workspaceId) {
        ownedWorkspace(userId, workspaceId);
        return chatbotRepository.findByWorkspaceId(workspaceId).stream()
                .map(c -> new ChatbotDtos.ChatbotResponse(c.getId(), workspaceId, c.getName(), c.getModel(), c.getCreatedAt()))
                .toList();
    }

    public ChatDtos.ChatResponse chat(String userId, ChatDtos.ChatRequest request) {
        Chatbot chatbot = chatbotRepository.findById(request.chatbotId())
                .orElseThrow(() -> new EntityNotFoundException("Chatbot not found"));
        if (!chatbot.getWorkspace().getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("No permission for chatbot");
        }

        ChatSession session = request.sessionId() == null
                ? createSession(chatbot, userId)
                : sessionRepository.findById(request.sessionId()).orElseThrow(() -> new EntityNotFoundException("Session not found"));

        saveMessage(session, "user", request.message());

        List<Double> queryEmbedding = aiGatewayService.embed(request.message());
        List<EmbeddingChunk> chunks = embeddingChunkRepository.findByDocumentWorkspaceId(chatbot.getWorkspace().getId());

        String context = chunks.stream()
                .map(chunk -> new ScoredChunk(chunk.getChunkText(), VectorMath.cosineSimilarity(queryEmbedding, readVector(chunk.getVectorJson()))))
                .sorted(Comparator.comparing(ScoredChunk::score).reversed())
                .limit(4)
                .map(ScoredChunk::text)
                .reduce("", (a, b) -> a + "\n\n" + b);

        String finalPrompt = "다음 지식기반 컨텍스트를 우선 참고해서 답변하세요:\n" + context + "\n\n사용자 질문:\n" + request.message();
        String answer = aiGatewayService.chat(chatbot.getModel(), chatbot.getSystemPrompt(), finalPrompt);
        saveMessage(session, "assistant", answer);

        return new ChatDtos.ChatResponse(session.getId(), answer);
    }

    private Workspace ownedWorkspace(String userId, Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found"));
        if (!workspace.getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("No permission for workspace");
        }
        return workspace;
    }

    private ChatSession createSession(Chatbot chatbot, String userId) {
        ChatSession session = new ChatSession();
        session.setChatbot(chatbot);
        session.setUserId(userId);
        return sessionRepository.save(session);
    }

    private void saveMessage(ChatSession session, String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setSession(session);
        message.setRole(role);
        message.setContent(content);
        messageRepository.save(message);
    }

    private List<String> splitChunks(String text, int chunkSize) {
        return text.lines()
                .flatMap(line -> {
                    if (line.length() <= chunkSize) {
                        return java.util.stream.Stream.of(line);
                    }
                    return java.util.stream.IntStream.range(0, (line.length() + chunkSize - 1) / chunkSize)
                            .mapToObj(i -> line.substring(i * chunkSize, Math.min(line.length(), (i + 1) * chunkSize)));
                }).filter(s -> !s.isBlank())
                .toList();
    }

    private String writeVector(List<Double> vector) {
        try {
            return objectMapper.writeValueAsString(vector);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private List<Double> readVector(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private record ScoredChunk(String text, double score) {}
}
