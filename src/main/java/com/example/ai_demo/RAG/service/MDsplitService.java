package com.example.ai_demo.RAG.service;

import com.example.ai_demo.RAG.enumlist.ErrorEnum;
import com.example.ai_demo.RAG.exception.AI_DemoException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class MDsplitService {

    @Resource
    private VectorStore vectorStore;

    @Resource
    private PgVectorStore pgVectorStore;

    public void MDsplit(String PayName, MultipartFile file) throws AI_DemoException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            StringBuilder contentBuilder = new StringBuilder();
            String currentSection = null;
            int currentHeadingLevel = Integer.MAX_VALUE; // 跟踪當前標題層級
            List<Document> info = new ArrayList<>();
            Map<String, Object> docMap = new LinkedHashMap<>();

            // 正則表達式匹配標題（如 ##, ###, ####），但排除 # 標題
            Pattern headingPattern = Pattern.compile("^(#+)\\s*(.+)");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = headingPattern.matcher(line);

                if (matcher.matches()) {
                    // 取得標題層級與標題文字
                    int headingLevel = matcher.group(1).length();
                    String headingText = matcher.group(2);

                    // 排除 # 等級的標題（即 headingLevel == 1）
                    if (headingLevel == 1) {
                        continue; // 跳過這一行，忽略 # 標題
                    }

                    // 當遇到相同或更高等級的標題時，先將當前段落寫入
                    if (headingLevel <= currentHeadingLevel) {
                        if (currentSection != null) {
                            // 當遇到同級別或更高級別的標題，將之前的段落內容寫入
                            log.info("章節標題: " + currentSection + "\n" + contentBuilder.toString().trim() + "\n" + "---------" + currentSection + " END ----------");

                            docMap.put("PayName", PayName);
                            docMap.put("Title", currentSection);

                            if (pgVectorStore.SearchMataData(currentSection, PayName) != null) {
                                throw new AI_DemoException(ErrorEnum.REPEATED_WRITUNG_ERROR, ErrorEnum.REPEATED_WRITUNG_ERROR.getMessage());
                            }

                            info.add(new Document(currentSection + "\n" + contentBuilder.toString(), docMap));
                            vectorStore.add(info);
                            info.clear(); // 清除已加入的資料
                        }

                        // 開始新段落
                        currentSection = headingText;
                        currentHeadingLevel = headingLevel; // 更新當前標題層級
                        contentBuilder.setLength(0); // 清空內容
                    }

                    // 無論是同級還是較小級別標題，都應加入當前段落
                    contentBuilder.append(matcher.group(0)).append("\n"); // 將標題也加到內容中
                } else {
                    // 如果不是標題，將行附加到當前段落內容
                    if (currentSection != null) {
                        contentBuilder.append(line).append("\n");
                    }
                }
            }

            // 最後一個章節寫入
            if (currentSection != null) {
                log.info("章節標題: " + currentSection + "\n" + contentBuilder.toString().trim() + "\n" + "---------" + currentSection + " END ----------");

                docMap.put("PayName", PayName);
                docMap.put("Title", currentSection);


                if (pgVectorStore.SearchMataData(currentSection, PayName) != null) {
                    throw new AI_DemoException(ErrorEnum.REPEATED_WRITUNG_ERROR, ErrorEnum.REPEATED_WRITUNG_ERROR.getMessage());
                }

                info.add(new Document(currentSection + "\n" + contentBuilder.toString(), docMap));
                vectorStore.add(info);
            }

            log.info("========讀取完畢========");

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    // 模板，這裡假設已經有一個 PromptTemplate 類
//    private final PromptTemplate template = new PromptTemplate("## {PayName}\n\n{content}");
//
//    public void MDsplit(MultipartFile file) {
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
//            String line;
//            StringBuilder contentBuilder = new StringBuilder();
//            String currentSection = null;
//            int currentHeadingLevel = Integer.MAX_VALUE; // 跟踪當前標題層級
//            List<Document> info = new ArrayList<>();
//
//            // 正則表達式匹配標題（如 ##, ###, ####），但排除 # 標題
//            Pattern headingPattern = Pattern.compile("^(#+)\\s*(.+)");
//            // JSON 格式的正則表達式
//            Pattern jsonPattern = Pattern.compile("\\{\\s*\".*?\"\\s*:\\s*\".*?\"\\s*\\}");
//
//            while ((line = reader.readLine()) != null) {
//                Matcher matcher = headingPattern.matcher(line);
//                Matcher jsonMatcher = jsonPattern.matcher(line);
//
//                if (jsonMatcher.find()) {
//                    // 如果偵測到 JSON 內容，直接保存
//                    log.info("Detected JSON block: " + line);
//                    contentBuilder.append(line).append("\n"); // 保持原樣
//                } else if (matcher.matches()) {
//                    // 取得標題層級與標題文字
//                    int headingLevel = matcher.group(1).length();
//                    String headingText = matcher.group(2);
//
//                    // 排除 # 等級的標題（即 headingLevel == 1）
//                    if (headingLevel == 1) {
//                        continue; // 跳過這一行，忽略 # 標題
//                    }
//
//                    // 當遇到相同或更高等級的標題時，先將當前段落寫入
//                    if (headingLevel <= currentHeadingLevel) {
//                        if (currentSection != null) {
//                            // 當遇到同級別或更高級別的標題，將之前的段落內容寫入
//                            String renderedContent = template.render(Map.of(
//                                    "PayName", currentSection,
//                                    "content", contentBuilder.toString().trim()
//                            ));
//
//                            log.info("章節標題: " + currentSection + "\n" + contentBuilder.toString().trim() + "\n" + "---------" + currentSection + " END ----------");
//
//                            info.add(new Document(renderedContent));
//                            vectorStore.add(info);
//                            info.clear(); // 清除已加入的資料
//                        }
//
//                        // 開始新段落
//                        currentSection = headingText;
//                        currentHeadingLevel = headingLevel; // 更新當前標題層級
//                        contentBuilder.setLength(0); // 清空內容
//                    }
//
//                    // 無論是同級還是較小級別標題，都應加入當前段落
//                    contentBuilder.append(matcher.group(0)).append("\n"); // 將標題也加到內容中
//                } else {
//                    // 如果不是標題，將行附加到當前段落內容
//                    if (currentSection != null) {
//                        contentBuilder.append(line).append("\n");
//                    }
//                }
//            }
//
//            // 最後一個章節寫入
//            if (currentSection != null) {
//                String renderedContent = template.render(Map.of(
//                        "PayName", currentSection,
//                        "content", contentBuilder.toString().trim()
//                ));
//
//                log.info("章節標題: " + currentSection + "\n" + contentBuilder.toString().trim() + "\n" + "---------" + currentSection + " END ----------");
//
//                info.add(new Document(renderedContent));
//                vectorStore.add(info);
//            }
//
//            log.info("========讀取完畢========");
//
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//        }
//    }
}
