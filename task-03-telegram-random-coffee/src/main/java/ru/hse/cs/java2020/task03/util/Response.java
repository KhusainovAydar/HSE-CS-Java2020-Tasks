package ru.hse.cs.java2020.task03.util;

public class Response {
    private String content;
    private Integer statusCode;

    public Response(String content, Integer statusCode) {
        this.content = content;
        this.statusCode = statusCode;
    }

    public String getContent() {
        return content;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
