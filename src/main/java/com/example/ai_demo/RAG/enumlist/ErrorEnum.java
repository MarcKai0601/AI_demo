package com.example.ai_demo.RAG.enumlist;

public enum ErrorEnum{

    SUCCESS("0000","success"),

    FILE_NULL("0001", "File can't null"),

    REPEATED_WRITUNG_ERROR("0002", "Repeated writting error")

    ;


    private String code;
    private String message;

    ErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
