package com.example.ai_demo.RAG;

import org.w3c.dom.Node;

import javax.swing.text.html.parser.Parser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/四方金流文件.md"));
            String line;
            StringBuilder contentBuilder = new StringBuilder();
            String currentSection = null;

            // Regular expression to match headings (e.g., ####, ###, ##, #)
            Pattern headingPattern = Pattern.compile("^(#+)\\s*(.+)");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = headingPattern.matcher(line);

                if (matcher.matches()) {
                    // Check if heading level is 4 or higher
                    if (matcher.group(1).length() >= 4) {
                        // If there is a current section, print it out
                        if (currentSection != null) {
                            System.out.println("章節標題: " + currentSection);
                            System.out.println(contentBuilder.toString().trim());
                            System.out.println("---------" + currentSection + " END ----------");
                        }

                        // Start a new section
                        currentSection = matcher.group(2); // Get the heading title
                        contentBuilder.setLength(0); // Clear the content for the new section
                    }
                } else {
                    // If it's not a heading, append the line to the current section content
                    if (currentSection != null) {
                        contentBuilder.append(line).append("\n");
                    }
                }
            }

//            // Print the last section
//            if (currentSection != null) {
//                System.out.println("章節標題: " + currentSection);
//                System.out.println(contentBuilder.toString().trim());
//                System.out.println("-------------------");
//            }

            reader.close();
            System.out.println("========讀取完畢========");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
