package com.application.pdfapplication.controller;

import com.application.pdfapplication.service.WordDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class WordDataController implements WordBooksApi, TestOptionsApi {

    private final WordDataService wordDataService;

    @Autowired
    public WordDataController(WordDataService wordBooksService) {
        this.wordDataService = wordBooksService;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return WordBooksApi.super.getRequest();
    }

    @Override
    public ResponseEntity<List<String>> getWordBooks() {
        return wordDataService.getWordBooksService();
    }

    @Override
    public ResponseEntity<Map<String, Long>> getSectionAndWordCount(String wordBook) {
        return wordDataService.getSectionAndWordCountMapService(wordBook);
    }
}
