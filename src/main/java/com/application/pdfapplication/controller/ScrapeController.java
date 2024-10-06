package com.application.pdfapplication.controller;

import com.application.pdfapplication.service.ScrapeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScrapeController implements ScrapeApi {

    private final ScrapeService scrapeService;

    @Autowired
    public ScrapeController(ScrapeService scrapeService) {
        this.scrapeService = scrapeService;
    }

    @Override
    public ResponseEntity<Void> scrapeDataAndSave(String url, String wordNumHeader, String wordHeader, String wordMeaningHeader) {
        return scrapeService.scrapeWordService(url, wordNumHeader, wordHeader, wordMeaningHeader);
    }
}
