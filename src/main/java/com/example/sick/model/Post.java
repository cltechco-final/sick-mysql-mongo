package com.example.sick.model;

import lombok.Data;

@Data
public class Post {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String password; // 취약점: 평문으로 저장되는 비밀번호
} 