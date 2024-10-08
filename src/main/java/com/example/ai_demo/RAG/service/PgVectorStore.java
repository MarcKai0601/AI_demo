package com.example.ai_demo.RAG.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PgVectorStore implements VectorStore {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public PgVectorStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }


    @Override
    public void add(List<Document> documents) {

    }

    @Override
    public Optional<Boolean> delete(List<String> idList) {
        return Optional.empty();
    }

    @Override
    public List<Document> similaritySearch(SearchRequest request) {
        // 取得查詢字符串
        String string = request.getQuery();

        // 自定義SQL查詢邏輯，進行模糊匹配
        String sql = "SELECT * FROM vector_store WHERE content LIKE ?";

        // 動態插入查詢參數，這裡使用 query 字符串進行模糊查詢
        List<Document> documents = jdbcTemplate.query(
                sql,
                new Object[]{"%" + string + "%"},  // 將查詢字串包裹在百分號內
                (rs, rowNum) -> {
                    // 將結果集映射為 Document 對象
                    return new Document(rs.getString("content"));
                }
        );

        System.out.println(documents);
        return documents;
    }

    public List<String> SearchMataData(String title, String payName) {
        // 使用 JSON 查詢運算符進行模糊匹配
        String sql = "SELECT * FROM vector_store WHERE metadata ->> 'Title' LIKE ? AND metadata ->> 'PayName' LIKE ?";

        System.out.println(sql);

        // 動態插入查詢參數，這裡使用模糊查詢
        List<String> documents = jdbcTemplate.query(
                sql,
                new Object[]{"%" + title + "%", "%" + payName + "%"},  // 傳遞查詢參數
                (rs, rowNum) -> {
                    // 檢查 metadata 是否為 null，處理為空情況
                    String content = rs.getString("content");
                    String metadataJson = rs.getString("metadata");

                    Map<String, Object> metadata = new HashMap<>();

                    if (metadata != null) {
                        try {
                            metadata = objectMapper.readValue(metadataJson, new TypeReference<Map<String, Object>>() {
                            });
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // 將結果集映射為 Document 對象
                    Document document = new Document(content, metadata);

                    return document.getId();
                }
        );

        if (documents.size() <= 0) {return null;}

        System.out.println(documents);
        return documents;
    }


}
