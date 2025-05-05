package com.example.sick.controller;

import com.example.sick.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 취약점 1: SQL Injection
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchPosts(@RequestParam String keyword) {
        String query = "SELECT * FROM posts WHERE title LIKE '%" + keyword + "%'";
        List<Map<String, Object>> posts = jdbcTemplate.queryForList(query);
        return ResponseEntity.ok(posts);
    }

    // 취약점 2: XSS 취약점
    @PostMapping
    public ResponseEntity<Void> createPost(@RequestBody Post post) {
        String query = "INSERT INTO posts (title, content, author, password) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(query, post.getTitle(), post.getContent(), post.getAuthor(), post.getPassword());
        return ResponseEntity.ok().build();
    }

    // 취약점 3: 파일 경로 조작 취약점
    @GetMapping("/file")
    public ResponseEntity<String> getFile(@RequestParam String filename) {
        File file = new File("/uploads/" + filename);
        try (FileInputStream fis = new FileInputStream(file)) {
            // 파일 처리
            return ResponseEntity.ok("File processed successfully");
        } catch (IOException e) {
            e.printStackTrace(); // 취약점 4: 상세 에러 노출
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // 취약점 5: 입력값 검증 부족
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable Long id, @RequestBody String content) {
        int age = Integer.parseInt(content); // 숫자가 아닌 입력에 대한 검증 없음
        String query = "UPDATE posts SET content = ? WHERE id = ?";
        jdbcTemplate.update(query, content, id);
        return ResponseEntity.ok().build();
    }

    // 추가: 모든 게시글 조회
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllPosts() {
        String query = "SELECT * FROM posts";
        List<Map<String, Object>> posts = jdbcTemplate.queryForList(query);
        return ResponseEntity.ok(posts);
    }

    // 추가: 특정 게시글 조회
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPost(@PathVariable Long id) {
        String query = "SELECT * FROM posts WHERE id = ?";
        Map<String, Object> post = jdbcTemplate.queryForMap(query, id);
        return ResponseEntity.ok(post);
    }

    // 추가: 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        String query = "DELETE FROM posts WHERE id = ?";
        jdbcTemplate.update(query, id);
        return ResponseEntity.ok().build();
    }
} 