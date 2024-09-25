package com.example.ai_demo.RAG.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class MDsplitService {

    @Resource
    private VectorStore vectorStore;

    public void MDsplit(MultipartFile file) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            StringBuilder contentBuilder = new StringBuilder();
            String currentSection = null;
            List<Document> info = new ArrayList<>();

            // 正则表达式匹配标题（如 ####, ###, ##, #）
            Pattern headingPattern = Pattern.compile("^(#+)\\s*(.+)");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = headingPattern.matcher(line);

                if (matcher.matches()) {
                    // 检查标题级别是否为4或更高
                    if (matcher.group(1).length() >= 4) {
                        // 如果有当前章节，打印出来
                        if (currentSection != null) {

                            log.info("章節標題: " + currentSection + "/n" + contentBuilder.toString().trim() + "/n" + "---------" + currentSection + " END ----------");

                            info.add(new Document(currentSection + contentBuilder.toString()));
                            vectorStore.add(info);
                        }

                        // 开始一个新章节
                        currentSection = matcher.group(2); // 获取标题
                        contentBuilder.setLength(0); // 清空内容
                    }
                } else {
                    // 如果不是标题，将行附加到当前章节内容
                    if (currentSection != null) {
                        contentBuilder.append(line).append("\n");
                    }
                }
            }

            log.info("========讀取完畢========");

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
