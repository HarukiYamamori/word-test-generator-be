package com.application.pdfapplication.controller;

import com.application.pdfapplication.model.GeneratePdfRequest;
import com.application.pdfapplication.service.GeneratePdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneratePdfController implements GeneratePdfApi {

    private final GeneratePdfService pdfService;

    @Autowired
    public GeneratePdfController(GeneratePdfService pdfService) {
        this.pdfService = pdfService;
    }

    @Override
    @CrossOrigin(origins = "http://localhost:63343")
    public ResponseEntity<Resource> generatePdf(GeneratePdfRequest generatePdfRequest) {
        return pdfService.generatePdf(generatePdfRequest);
    }
}
