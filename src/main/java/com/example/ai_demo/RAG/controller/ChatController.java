package com.example.ai_demo.RAG.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@Slf4j
@RestController("/ai/chat")
public class ChatController {

    private OllamaChatModel chatModel;

    @Autowired
    public ChatController(OllamaChatModel chatModel){
        this.chatModel = chatModel;
    }

    @GetMapping("/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "个人小金额投资者如何进行沪深主板投资") String message) {
        return Map.of("generation", chatModel.call(message));
    }

    @GetMapping("/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "个人小金额投资者如何进行沪深主板投资") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }
}
