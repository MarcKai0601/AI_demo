package com.example.ai_demo.RAG.controller.postagres;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddpostagresFileRequest {

    @NotBlank(message = "PayName cannot be blank")
    private String PayName;

    @NotEmpty(message = "File cannot be empty")
    private MultipartFile File;

}
