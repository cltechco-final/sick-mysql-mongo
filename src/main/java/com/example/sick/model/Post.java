package com.example.sick.model;

import lombok.Data;

@Data
public class Post {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String password;
} 
