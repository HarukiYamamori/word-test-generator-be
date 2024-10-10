package com.application.pdfapplication.service;

import com.application.pdfapplication.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class WordDataService {


    @Autowired
    private final WordRepository wordRepository;

    public WordDataService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    public ResponseEntity<List<String>> getWordBooksService() {
        List<String> wordBookList = wordRepository.findAllDistinctWordBooks();
        return ResponseEntity.ok(wordBookList);
    }

    public ResponseEntity<Map<String, Long>> getSectionAndWordCountMapService(String wordBook) {
        Map<String, Long> sectionWordCountMap = new LinkedHashMap<>();

        List<Object[]> results = wordRepository.findSectionsByWordBook(wordBook);
        for(Object[] result : results) {
            String section = (String) result[0];
            Long wordCount = (Long) result[1];
            sectionWordCountMap.put(section, wordCount);
        }

        return ResponseEntity.ok(sectionWordCountMap);
    }
}
