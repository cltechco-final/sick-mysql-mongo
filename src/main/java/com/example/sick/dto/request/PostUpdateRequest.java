package com.example.sick.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 수정")
public class PostUpdateRequest {
    @Schema(description = "내용", example = "I Love NewJeans Hanni !")
    private String content;
}
