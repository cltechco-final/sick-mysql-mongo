package com.example.sick.controller;

import com.example.sick.dto.request.PostRequest;
import com.example.sick.dto.request.PostUpdateRequest;
import com.example.sick.dto.response.PostResponse;
import com.example.sick.dto.response.PostUpdateResponse;
import com.example.sick.model.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<PostResponse> postRowMapper = (rs, rowNum) -> new PostResponse(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getString("author"),
            rs.getString("password")
    );

    // 취약점 1: SQL Injection
    @Operation(
            summary = "키워드 기반 게시글 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PostResponse.class)),
                                    examples = @ExampleObject(
                                            name = "Post List Example",
                                            summary = "예시 게시글 리스트",
                                            value = """
                        [
                            {
                                "id": 1,
                                "title": "Hello World",
                                "content": "예시 게시글 내용입니다.",
                                "author": "jinjoowon",
                                "password": "secret"
                            },
                            {
                                "id": 2,
                                "title": "New Post",
                                "content": "또 다른 예시입니다.",
                                "author": "ktuser",
                                "password": "anotherpass"
                            }
                        ]
                        """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/search")
    public ResponseEntity<List<PostResponse>> searchPosts(@Parameter(description = "검색 키워드", example = "NewJeans") @RequestParam String keyword) {
        String query = "SELECT * FROM posts WHERE title LIKE '%" + keyword + "%'";
        List<PostResponse> posts = jdbcTemplate.query(query, postRowMapper);
        return ResponseEntity.ok(posts);
    }

    // 취약점 2: XSS 취약점
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest post) {
        String query = "INSERT INTO posts (title, content, author, password) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getAuthor());
            ps.setString(4, post.getPassword());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;

        PostResponse response = new PostResponse(id, post.getTitle(), post.getContent(), post.getAuthor(), post.getPassword());
        return ResponseEntity.ok(response);
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
    public ResponseEntity<PostUpdateResponse> updatePost(@Parameter(description = "게시글 ID", example = "1") @PathVariable Long id, @RequestBody PostUpdateRequest content) {
        int age = Integer.parseInt(content.getContent()); // 숫자가 아닌 입력에 대한 검증 없음
        String query = "UPDATE posts SET content = ? WHERE id = ?";
        jdbcTemplate.update(query, content, id);
        return ResponseEntity.ok(
                new PostUpdateResponse(
                        id,
                        content.getContent()
                )
        );
    }

    // 추가: 모든 게시글 조회
    @Operation(
            summary = "게시글 검색",
            parameters = {
                    @Parameter(name = "keyword", description = "검색 키워드", required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "검색 결과",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PostResponse.class)),
                                    examples = @ExampleObject(
                                            name = "Post Search Example",
                                            summary = "예시 검색 결과",
                                            value = """
                        [
                            {
                                "id": 1,
                                "title": "Hello World",
                                "content": "예시 게시글 내용입니다.",
                                "author": "jinjoowon",
                                "password": "secret"
                            }
                        ]
                        """
                                    )
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        String query = "SELECT * FROM posts";
        List<PostResponse> posts = jdbcTemplate.query(query, postRowMapper);
        return ResponseEntity.ok(posts);
    }

    // 추가: 특정 게시글 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@Parameter(description = "게시글 ID", example = "1") @PathVariable Long id) {
        String query = "SELECT * FROM posts WHERE id = ?";
        PostResponse post = jdbcTemplate.queryForObject(query, postRowMapper, id);
        return ResponseEntity.ok(post);
    }

    // 추가: 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@Parameter(description = "게시글 ID", example = "1") @PathVariable Long id) {
        String query = "DELETE FROM posts WHERE id = ?";
        jdbcTemplate.update(query, id);
        return ResponseEntity.ok().build();
    }
}
