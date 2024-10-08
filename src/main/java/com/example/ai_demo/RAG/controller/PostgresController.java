package com.example.ai_demo.RAG.controller;

import com.example.ai_demo.RAG.enumlist.ErrorEnum;
import com.example.ai_demo.RAG.exception.AI_DemoException;
import com.example.ai_demo.RAG.service.MDsplitService;
import com.example.ai_demo.RAG.service.PgVectorStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import javax.swing.event.ListDataEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    @Autowired
    private MDsplitService mDsplitService;

    @Autowired
    private PgVectorStore pgVectorStore;

//    @PostMapping("/analysisPDF")
//    public String analysisPDF(@RequestParam(value = "file") MultipartFile file) {
//        try (PDDocument document = Loader.loadPDF(RandomAccessReadBuffer.createBufferFromStream(file.getInputStream()))) {
//            // Check if the PDF is encrypted
//            List<Document> info = new ArrayList<>();
//            if (!document.isEncrypted()) {
//                // Use PDFTextStripper to extract the text
//                PDFTextStripper pdfStripper = new PDFTextStripper();
//
//                // Extract text from the PDF
////                String text = pdfStripper.getText(document);
//                for (int pageNumber = 1; pageNumber <= document.getNumberOfPages(); pageNumber++) {
//                    pdfStripper.setStartPage(pageNumber);
//                    pdfStripper.setEndPage(pageNumber);
//                    info.add(new Document(pdfStripper.getText(document)));
//                }
//                vectorStore.add(info);
//            } else {
//                log.info("The PDF is encrypted, cannot extract text.");
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//        return "PDF OK";
//    }

//    @PostMapping("/analysisPDF")
//    public String analysisPDF(@RequestParam(value = "file") MultipartFile file) {
//        if (file.isEmpty()) {
//            return "File is empty";
//        }
//
//        try {
//            // 将 MultipartFile 转换为 PDF 文件处理
//            PDDocument document = Loader.loadPDF(RandomAccessReadBuffer.createBufferFromStream(file.getInputStream()));
//            List<Document> info = new ArrayList<>();
//
//            // 使用 PDFTextStripper 提取文本
//            PDFTextStripper pdfStripper = new PDFTextStripper();
//            String pdfText = pdfStripper.getText(document);
//            String paragraph = null;
//
//            // 关闭文档
//            document.close();
//
//            // 定义正则表达式匹配大标题，假设大标题可能是以“方法”、“接口”等字结尾
//            Pattern titlePattern = Pattern.compile(".*(方法|接口).*");
//            Matcher matcher = titlePattern.matcher(pdfText);
//
//            // 找到每个标题以及它对应的段落
//            int lastMatchEnd = 0; // 用于记录上一个匹配的位置
//            String previousTitle = null; // 保存上一个标题的名称
//
//            StringBuilder result = new StringBuilder(); // 用于保存输出结果
//
//            while (matcher.find()) {
//                // 如果已经有一个标题了，输出上一个标题及其段落内容
//                if (previousTitle != null) {
//                    // 获取标题到下一个标题之间的内容作为段落
//                    paragraph = pdfText.substring(lastMatchEnd, matcher.start()).trim();
//                    log.info(paragraph + "\n");
//                    log.info("---------" + previousTitle + " END ----------\n");
//                }
//
//                // 更新 lastMatchEnd 和保存当前匹配的标题
//                lastMatchEnd = matcher.end();
//                previousTitle = matcher.group().trim(); // 获取当前标题
//
//
//                info.add(new Document(previousTitle + paragraph.toString()));
//                vectorStore.add(info);
//            }
//
//            // 输出最后一个标题及其段落（如果有的话）
//            if (previousTitle != null) {
//                paragraph = pdfText.substring(lastMatchEnd).trim();
//                log.info(paragraph + "\n");
//                log.info("---------" + previousTitle + " END ----------\n");
//            }
//
//            // 文件读取完毕标志
//            log.info("========讀取完畢========\n");
//
//            // 返回结果
//            return "PDF OK";
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Error processing PDF: " + e.getMessage();
//        }
//
//    }

//    @GetMapping("/ragMessage")
//    public String getMessageByRAG(@RequestParam(value = "message") String message) {
//
//        log.info("收到訊息 :" + message);
//
//        List<Document> documents = vectorStore.similaritySearch(message);
//
//        log.info("vectorStore Log :" + documents);
//        String base = """
//                你只會用中文回覆,
//                請使用以下資料作為參考進行回答%s
//                %s
//                """;
//        Prompt prompt = new Prompt(String.format(base, message, documents.stream().map(Document::getContent).collect(Collectors.joining("\n"))));
//        ChatResponse call = chatModel.call(prompt);
//        return call.getResult().getOutput().getContent();
//    }

    @GetMapping("/ragMessage")
    public String getMessageByRAG(@RequestParam(value = "message") String message) {
        log.info("收到訊息 :" + message);

        // 進行相似度搜索獲得相關文檔
        List<Document> documents = vectorStore.similaritySearch(message);
        log.info("vectorStore Log :" + documents);

        // 準備一個 PromptTemplate
        PromptTemplate template = new PromptTemplate("""
                你只會用中文回覆,
                請使用以下資料作為參考進行回答:
                問題: {message}
                參考資料: 
                {documentsContent}
                """);

        // 檢查 JSON 的正則表達式
        Pattern jsonPattern = Pattern.compile("\\{\\s*\".*?\"\\s*:\\s*\".*?\"\\s*\\}");

        // 處理每個 Document 的內容
        String documentContent = documents.stream()
                .map(doc -> {
                    String content = doc.getContent();
                    Matcher jsonMatcher = jsonPattern.matcher(content);

                    // 如果內容是 JSON，則保持原樣
                    if (jsonMatcher.find()) {
                        return content;
                    }

                    // 否則進行模板化處理
                    return content;
                })
                .collect(Collectors.joining("\n")); // 將所有內容合併為一個字符串

        // 將變數打包到 Map 中
        Map<String, Object> variables = new HashMap<>();
        variables.put("message", message);  // 直接存放 String
        variables.put("documentsContent", documentContent); // 直接存放 String

        // 使用 PromptTemplate 渲染最終的提示
        String finalPrompt = template.render(variables);
        System.out.println("finalPrompt : " + finalPrompt);  // 查看最終渲染的結果

        Prompt prompt = new Prompt(finalPrompt);

        // 呼叫 chatModel 來獲取最終回應
        ChatResponse call = chatModel.call(prompt);
        return call.getResult().getOutput().getContent();
    }


    @GetMapping("/ragMessageTest")
    public String getMessageByTest(@RequestParam(value = "message") String message) {

        List<Document> documents = pgVectorStore.similaritySearch(message);

        // 檢查 JSON 的正則表達式
        Pattern jsonPattern = Pattern.compile("\\{\\s*\".*?\"\\s*:\\s*\".*?\"\\s*\\}");

        String documentContent = documents.stream()
                .map(doc -> {
                    String content = doc.getContent();
                    Matcher jsonMatcher = jsonPattern.matcher(content);

                    // 如果內容是 JSON，則保持原樣
                    if (jsonMatcher.find()) {
                        return content;
                    }

                    // 否則進行模板化處理
                    return content;
                })
                .collect(Collectors.joining("\n")); // 將所有內容合併為一個字符串

        return documentContent;
    }


    @PostMapping("/analysisMD")
    public String analysisMD(@RequestParam(value = "PayName") String PayName, @RequestParam(value = "file") MultipartFile file) throws AI_DemoException {

//        if (file != null) {
//            throw new AI_DemoException(ErrorEnum.FILE_NULL,"File is null or empty");
//        }

        mDsplitService.MDsplit(PayName, file);

        return "MD OK";
    }

    @PostMapping("/Search")
    public List<String> Search(@RequestParam(value = "Title") String Title, @RequestParam(value = "PayName") String PayName) throws AI_DemoException {

//        if (file != null) {
//            throw new AI_DemoException(ErrorEnum.FILE_NULL,"File is null or empty");
//        }


        return pgVectorStore.SearchMataData(Title, PayName);
    }

}
