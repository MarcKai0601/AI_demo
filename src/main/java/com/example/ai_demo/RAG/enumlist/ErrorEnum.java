package com.example.ai_demo.RAG.enumlist;

public enum ErrorEnum{

    SUCCESS("0000","success"),

    FILE_NULL("0001", "File can't null"),

    REPEATED_WRITUNG_ERROR("0002", "Repeated writting error"),
    PAYNAME_EMPTY_ERROR("0003", "Payname is empty"),
    FILE_EMPTY_ERROR("0004", "File is empty"),

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
