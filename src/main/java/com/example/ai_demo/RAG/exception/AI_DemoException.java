package com.example.ai_demo.RAG.exception;

import com.example.ai_demo.RAG.enumlist.ErrorEnum;

public class AI_DemoException extends Exception{
   
    private ErrorEnum code;

    public AI_DemoException(ErrorEnum code, Object message, Throwable cause) {
        super(code.getMessage() + " -> " + message, cause);
        this.code = code;
    }

    public AI_DemoException(ErrorEnum code, Object message) {
        super(code.getMessage() + " -> " + message);
        this.code = code;
    }

    public AI_DemoException(ErrorEnum code, Throwable cause) {
        super(code.getMessage(), cause);
        this.code = code;
    }

    public AI_DemoException(ErrorEnum code) {
        super(code.getMessage());
        this.code = code;
    }

    public ErrorEnum getCode() {
        return this.code;
    }
}
