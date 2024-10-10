package com.application.pdfapplication.controller;

import com.application.pdfapplication.model.GenerateTestRequest;
import com.application.pdfapplication.service.CreateTestService;
import com.application.pdfapplication.service.WordDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateTestController implements CreateTestApi {

    private final CreateTestService createTestService;

    @Autowired
    public CreateTestController(CreateTestService createTestService) {
        this.createTestService = createTestService;
    }

    @Override
    public ResponseEntity<Resource> generateTest(GenerateTestRequest generateTestRequest) {
        return createTestService.createTestService(generateTestRequest);
    }
}
