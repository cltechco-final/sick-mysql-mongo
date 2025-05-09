package com.example.sick.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 정보")
public class PostRequest {
    @Schema(description = "제목", example = "NewJeans I Love You")
    private String title;

    @Schema(description = "내용", example = "This is a sample post.")
    private String content;

    @Schema(description = "작성자", example = "jinjoowon")
    private String author;

    @Schema(description = "비밀번호", example = "jinja1234")
    private String password;
}
