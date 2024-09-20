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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController("/ai/postagres")
public class PostgresController {

    private VectorStore vectorStore;

    @Autowired
    public PostgresController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

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

}
