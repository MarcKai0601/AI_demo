package com.example.ai_demo.RAG.exceptionhandler;

import com.example.ai_demo.RAG.exception.AI_DemoException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AI_DemoException.class)
    public ResponseEntity<String> handleAIDemoException(AI_DemoException ex) {
        // 返回自定义错误消息和 HTTP 状态码
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + ex.getMessage());
    }

    // 你可以继续添加更多的 @ExceptionHandler 来处理其他类型的异常
}