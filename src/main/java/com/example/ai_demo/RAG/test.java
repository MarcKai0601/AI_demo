package com.example.ai_demo.RAG;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.w3c.dom.Node;

import javax.swing.text.html.parser.Parser;
import java.io.*;

import org.springframework.ai.document.Document;


public class test {

//    public static void main(String[] args) {
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/四方金流文件.md"));
//            String line ;
//            while ((line = reader.readLine()) != null){
//                System.out.println(line);
//            }
//            reader.close();
//
//            System.out.println("========讀取完畢========");
//            Parser parser = new Parser(line);
//            Node document = parser.parse("### 支付下单接口");
//
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//
//
//
//    }

//    public static void main(String[] args) {
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/四方金流文件.md"));
//            String line;
//            StringBuilder contentBuilder = new StringBuilder();
//            String currentSection = null;
//
//            // Regular expression to match headings (e.g., ####, ###, ##, #)
//            Pattern headingPattern = Pattern.compile("^(#+)\\s*(.+)");
//
//            while ((line = reader.readLine()) != null) {
//                Matcher matcher = headingPattern.matcher(line);
//
//                if (matcher.matches()) {
//                    // Check if heading level is 4 or higher
//                    if (matcher.group(1).length() >= 4) {
//                        // If there is a current section, print it out
//                        if (currentSection != null) {
//                            System.out.println("章節標題: " + currentSection);
//                            System.out.println(contentBuilder.toString().trim());
//                            System.out.println("---------" + currentSection + " END ----------");
//                        }
//
//                        // Start a new section
//                        currentSection = matcher.group(2); // Get the heading title
//                        contentBuilder.setLength(0); // Clear the content for the new section
//                    }
//                } else {
//                    // If it's not a heading, append the line to the current section content
//                    if (currentSection != null) {
//                        contentBuilder.append(line).append("\n");
//                    }
//                }
//            }
//
////            // Print the last section
////            if (currentSection != null) {
////                System.out.println("章節標題: " + currentSection);
////                System.out.println(contentBuilder.toString().trim());
////                System.out.println("-------------------");
////            }
//
//            reader.close();
//            System.out.println("========讀取完畢========");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//
//    public String convertPdfToMarkdown(String pdfFilePath) throws IOException {
//        // 使用靜態方法 PDDocument.load 來加載 PDF 文件
//        try (PDDocument document = Loader.loadPDF(new File(pdfFilePath))) {
//            PDFTextStripper pdfStripper = new PDFTextStripper();
//            // 提取文本
//            String extractedText = pdfStripper.getText(document);
//
//            // 將提取的文本轉換為 Markdown 格式
//            return convertTextToMarkdown(extractedText);
//        }
//    }
//
//    private String convertTextToMarkdown(String text) {
//        return text.replace("\n", "  \n"); // 將換行轉為 Markdown 的換行格式
//    }
//
////    private String convertTextToMarkdown(String text) {
////        // 將 ## 開頭的標題轉換為 ####
////        return text.replace("##", "####").replace("\n", "  \n"); // Markdown 換行格式
////    }
//
//    public void writeMarkdownToFile(String markdownContent, String outputFilePath) throws IOException {
//        try (FileWriter writer = new FileWriter(outputFilePath)) {
//            writer.write(markdownContent);
//        }
//    }
//
//    public static void main(String[] args) {
//        test converter = new test();
//        String pdfFilePath = "src/main/resources/BussPay.docx.pdf"; // 替換成你的 PDF 文件路徑
//        String outputFilePath = "src/main/resources/output.md"; // 替換成輸出的 Markdown 文件路徑
//
//        try {
//            String markdownContent = converter.convertPdfToMarkdown(pdfFilePath);
//            converter.writeMarkdownToFile(markdownContent, outputFilePath);
//            System.out.println("Markdown 文件已生成：" + outputFilePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public static void main(String[] args) {
        // PDF 文件路径
        String pdfFilePath = "src/main/resources/BussPay.docx.pdf";

        try {
            // 加载 PDF 文件
            PDDocument document = Loader.loadPDF(new File(pdfFilePath));

            // 使用 PDFTextStripper 提取文本
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String pdfText = pdfStripper.getText(document);

            // 关闭文档
            document.close();

            // 打印提取的文本
            System.out.println("Extracted Text:\n" + pdfText);

            // 分割段落（基于两个换行符作为段落分隔符）
            String[] paragraphs = pdfText.split("\n\n");

            // 输出每个段落
            for (String paragraph : paragraphs) {
                System.out.println("Paragraph: " + paragraph.trim());
                System.out.println("----------------------------------");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
