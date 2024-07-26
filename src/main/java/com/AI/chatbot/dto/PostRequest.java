package com.AI.chatbot.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Content is mandatory")
    private String content;

    private List<String> imageUrls;
    private List<String> deletedImages;     // 게시글 업데이트 시, 삭제 대상 이미지들의 url
    private List<MultipartFile> file;       // 게시글 업데이트 시, 새로운 이미지 파일 리스트 추가
}
 