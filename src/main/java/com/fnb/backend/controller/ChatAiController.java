package com.fnb.backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fnb.backend.dto.Request.ChatAIRequest;
import com.fnb.backend.service.AI.ChatAiService;

@RestController
@RequestMapping("/api/chat-ai")
@AllArgsConstructor
public class ChatAiController {
    private final ChatAiService chatAiService;

    @PostMapping("/answer")
    public ResponseEntity<?> getAnswer(@RequestBody ChatAIRequest chatAIRequest) {
        return ResponseEntity.ok(chatAiService.generateAnswer(chatAIRequest));
    }
}