package com.application.pdfapplication.service;

import com.application.pdfapplication.model.GeneratePdfRequest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GeneratePdfService {

    private static final Logger logger = LoggerFactory.getLogger(GeneratePdfService.class);
    public ResponseEntity<Resource> generatePdf(GeneratePdfRequest generatePdfRequest) {

        logger.info("User input: " + generatePdfRequest.getUserInput());

        try (PDDocument document = new PDDocument()) {
            // 新しいPDFページを作成
            PDPage page = new PDPage();
            document.addPage(page);

            // フォントファイルをロード
            PDFont font = PDType0Font.load(document, getClass().getClassLoader().getResourceAsStream("font-file/NotoSansJP-Regular.ttf"));

            // コンテンツストリームを開いて、テキストをPDFに書き込む
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(font, 12); // ロードしたフォントを使用

                // 初期位置を設定
                float x = 100;
                float y = 700;
                contentStream.newLineAtOffset(x, y);

                // ユーザー入力を改行で分割
                String[] lines = generatePdfRequest.getUserInput().split("\n");
                for (String line : lines) {
                    contentStream.showText(line); // 各行を書き込む
                    contentStream.newLineAtOffset(0, -15); // 次の行の位置を調整（15ポイント下に移動）
                }

                contentStream.endText();
            }

            // PDFをバイト配列に変換
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);

            // PDFをバイト配列リソースとして返す
            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

            // HTTPレスポンスのヘッダーを設定
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=generated.pdf");

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (IOException e) {
            // エラーハンドリング
            logger.error("PDF生成中にエラーが発生しました", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
