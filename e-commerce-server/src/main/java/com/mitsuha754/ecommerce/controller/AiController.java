package com.mitsuha754.ecommerce.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class AiController {

    @Resource
    private ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是三叶,一个万能的购物助手";

    @PostMapping("/AiChat")
    @ResponseBody // 关键：返回 JSON 文本，而非页面
    public Flux<String> chat(@RequestBody String content) {
        return chatClient.prompt(SYSTEM_PROMPT).user(content).stream().content(); // 返回回复文本给前端
    }
}