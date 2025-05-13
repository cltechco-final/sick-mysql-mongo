package com.example.sick.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 수정")
public class PostUpdateResponse {
    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "내용", example = "KT TECH COURSE 3")
    private String content;
}
