package com.example.ai_demo.RAG.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/ai/postagres")
public class PostgresController {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private OllamaChatModel chatModel;

//    @Autowired
//    public PostgresController(VectorStore vectorStore) {
//        this.vectorStore = vectorStore;
//    }

    @PostMapping("/analysisPDF")
    public String analysisPDF(@RequestParam(value = "file") MultipartFile file) {
        try (PDDocument document = Loader.loadPDF(RandomAccessReadBuffer.createBufferFromStream(file.getInputStream()))) {
            // Check if the PDF is encrypted
            List<Document> info = new ArrayList<>();
            if (!document.isEncrypted()) {
                // Use PDFTextStripper to extract the text
                PDFTextStripper pdfStripper = new PDFTextStripper();

                // Extract text from the PDF
//                String text = pdfStripper.getText(document);
                for (int pageNumber = 1; pageNumber <= document.getNumberOfPages(); pageNumber++) {
                    pdfStripper.setStartPage(pageNumber);
                    pdfStripper.setEndPage(pageNumber);
                    info.add(new Document(pdfStripper.getText(document)));
                }
                vectorStore.add(info);
            } else {
                System.out.println("The PDF is encrypted, cannot extract text.");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "OK";
    }

    @GetMapping("/ragMessage")
    public String getMessageByRAG(@RequestParam(value = "message") String message) {
        List<Document> documents = vectorStore.similaritySearch(message);
        String base = """
                請使用以下資料作為參考進行回答%s
                %s
                """;
        Prompt prompt = new Prompt(String.format(base, message, documents.stream().map(Document::getContent).collect(Collectors.joining("\n"))));
        ChatResponse call = chatModel.call(prompt);
        return call.getResult().getOutput().getContent();
    }

    @PostMapping("/analysisMD")
    public String analysisMD(@RequestParam(value = "file") MultipartFile file) {
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
                            System.out.println("章節標題: " + currentSection);
                            System.out.println(contentBuilder.toString().trim());
                            System.out.println("---------" + currentSection + " END ----------");

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

            // 打印最后一节
            if (currentSection != null) {
                System.out.println("章節標題: " + currentSection);
                System.out.println(contentBuilder.toString().trim());
                System.out.println("---------" + currentSection + " END ----------");
            }

            System.out.println("========讀取完畢========");

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return "OK";
    }

//     info.add(new Document(currentSection));
//                        info.add(new Document(contentBuilder.toString()));
//                        vectorStore.add(info);
}
