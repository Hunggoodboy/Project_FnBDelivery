package com.fnb.backend.service.AI;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fnb.backend.dto.Request.ChatAIRequest;
import com.fnb.backend.dto.Response.ChatAIResponse;
import com.fnb.backend.entity.ChatAiHistory;
import com.fnb.backend.entity.Users;
import com.fnb.backend.repository.ChatAiHistoryRepository;
import com.fnb.backend.repository.UsersRepository;
import com.fnb.backend.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatAiService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ChatAiHistoryRepository chatAiHistoryRepository;
    private final UsersRepository usersRepository;

    @Value("${app.ai.primary-model}")
    private String primaryModel;

    @Value("${app.ai.fallback-model}")
    private String fallbackModel;

    public ChatAiService(ChatClient.Builder chatClient, VectorStore vectorStore,
                         ChatAiHistoryRepository chatAiHistoryRepository, UsersRepository usersRepository) {
        this.chatClient = chatClient
                 .defaultToolNames("createNewFavouriteRequest", "createNewOrderRequest")
                .build();
        this.vectorStore = vectorStore;
        this.chatAiHistoryRepository = chatAiHistoryRepository;
        this.usersRepository = usersRepository;
    }

    public ChatAIResponse generateAnswer(ChatAIRequest chatAIRequest) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Users currentUser = usersRepository.findById(currentUserId)
                                           .orElseThrow(() -> new RuntimeException("Vui lòng đăng nhập để chat"));

        String thisConversationId = currentUser.getUserId().toString() + "_" + chatAIRequest.getConversationId();

        String optimizedQuestion = rewriteQuestion(chatAIRequest.getQuestion(), thisConversationId);
        String ragContext = findAnswer(optimizedQuestion);

        String currentQuestionWithRag = "Câu hỏi: " + chatAIRequest.getQuestion() +
                "\nThông tin tham khảo từ cửa hàng: \n" + ragContext;

        String answer = callWithFallback(getListMessages(thisConversationId, currentQuestionWithRag));

        // Lưu lịch sử
        chatAiHistoryRepository.save(ChatAiHistory.builder()
                                                  .conversationId(thisConversationId).messageType("USER").content(chatAIRequest.getQuestion())
                                                  .user(currentUser).createdAt(LocalDateTime.now()).build());

        chatAiHistoryRepository.save(ChatAiHistory.builder()
                                                  .conversationId(thisConversationId).messageType("ASSISTANT").content(answer)
                                                  .user(currentUser).createdAt(LocalDateTime.now()).build());

        return ChatAIResponse.builder().answer(answer).build();
    }

    // ✅ Gọi primary model, nếu 503 tự động chuyển sang fallback
    private String callWithFallback(List<Message> messages) {
        try {
            return chatClient.prompt()
                             .options(OpenAiChatOptions.builder().model(primaryModel).build())
                             .messages(messages)
                             .call()
                             .content();
        } catch (TransientAiException e) {
            log.warn("⚠️ Model {} quá tải (503), chuyển sang fallback: {}", primaryModel, fallbackModel);
            return chatClient.prompt()
                             .options(OpenAiChatOptions.builder().model(fallbackModel).build())
                             .messages(messages)
                             .call()
                             .content();
        }
    }

    private List<Message> getListMessages(String conversationId, String currentQuestionWithRag) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage("Bạn là chủ tiệm bánh Healthy Bakery. Bạn đang nói chuyện với người yêu của mình. " +
                "Hãy trả lời ngọt ngào, tinh tế, xưng 'anh' và gọi cô ấy là 'em' hoặc 'người yêu của anh'. " +
                "Tư vấn về các loại bánh, lượng calo tốt cho sức khỏe dựa trên thông tin tham khảo. " +
                "Nếu bạn trả lời sai hoặc làm sai, hoặc người dùng có mắng bạn, bạn hãy xin lỗi công chúa Mic thật ngọt ngào" +
                "Đặc biệt, giá của bánh được tính bằng 'cái ôm'. Hãy nhớ đính kèm link sản phẩm dạng HTML: <a href=\"link\">Tên bánh</a> để em dễ click."));

        messages.addAll(addMessageBaseHistory(conversationId));
        messages.add(new UserMessage(currentQuestionWithRag));
        return messages;
    }

    private String rewriteQuestion(String question, String conversationId) {
        List<Message> history = addMessageBaseHistory(conversationId);
        if (history.isEmpty()) return question;

        List<Message> rewriteMessages = new ArrayList<>();
        rewriteMessages.add(new SystemMessage("Viết lại câu hỏi mới nhất thành câu tìm kiếm đầy đủ ngữ cảnh về các loại bánh healthy. KHÔNG trả lời."));
        rewriteMessages.addAll(history);
        rewriteMessages.add(new UserMessage("Câu cần viết lại: " + question));

        try {
            return chatClient.prompt()
                             .options(OpenAiChatOptions.builder().model(primaryModel).build())
                             .messages(rewriteMessages)
                             .call()
                             .content();
        } catch (TransientAiException e) {
            // rewrite lỗi thì trả câu gốc, không block user
            log.warn("⚠️ rewriteQuestion thất bại, dùng câu hỏi gốc");
            return question;
        }
    }

    private List<Message> addMessageBaseHistory(String conversationId) {
        List<Message> messages = new ArrayList<>();
        List<ChatAiHistory> chatAiHistories = chatAiHistoryRepository.findTop30ByConversationIdOrderByCreatedAtDesc(conversationId);
        Collections.reverse(chatAiHistories);
        for (ChatAiHistory h : chatAiHistories) {
            if (h.getMessageType().equals("USER")) messages.add(new UserMessage(h.getContent()));
            else messages.add(new AssistantMessage(h.getContent()));
        }
        return messages;
    }

    private String findAnswer(String question) {
        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest.builder().query(question).topK(4).similarityThreshold(0.5).build()
        );
        return similarDocuments.stream().map(docs -> {
            String productId = docs.getMetadata().get("productId").toString();
            return docs.getText() + "\nLink chi tiết: https://tiem-banh-chi-va-hung-vi5p.onrender.com/product-detail.html?id=" + productId;
        }).collect(Collectors.joining("\n---\n"));
    }
}