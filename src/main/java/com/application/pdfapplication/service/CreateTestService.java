package com.application.pdfapplication.service;

import com.application.pdfapplication.entity.Word;
import com.application.pdfapplication.model.GenerateTestRequest;
import com.application.pdfapplication.repository.WordRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
public class CreateTestService {

    private static final Logger logger = LoggerFactory.getLogger(CreateTestService.class);

    @Autowired
    private final WordRepository wordRepository;

    public CreateTestService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    public ResponseEntity<Resource> createTestService(GenerateTestRequest generateTestRequest) {
        String wordBook = generateTestRequest.getWordBook();
        List<String> sections = generateTestRequest.getSections();
        int wordNum = generateTestRequest.getWordNum();

        List<Word> words = wordRepository.findWordsByBookAndSection(wordBook, sections, wordNum);

        try (PDDocument document = new PDDocument()) {
            // 新しいPDFページを作成
            PDPage questionPage = new PDPage();
            document.addPage(questionPage);

            // フォントファイルをロード
            PDFont font = PDType0Font.load(document, getClass().getClassLoader().getResourceAsStream("font-file/MPLUS1p-Regular.ttf"), true);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, questionPage)) {
                contentStream.setFont(font, 12); // フォントサイズを設定

                int yPosition = 750; // 初期Y座標

                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText(words.get(1).wordBook + " (問題)");

                contentStream.newLineAtOffset(0, -35); // 問題タイトルの下に行を移動

                for (int i = 0; i < words.size(); i++) {
                    contentStream.showText((i + 1) + ". " + words.get(i).getWord());
                    contentStream.newLineAtOffset(0, -20); // 次の行のY位置を下げる
                    contentStream.showText("____________________");
                    contentStream.newLineAtOffset(0, -30); // 次の行のY位置を下げる
                }
                contentStream.endText(); // すべてのテキストを追加した後に呼び出す
            }

            // 新しいPDFページを作成
            PDPage answerPage = new PDPage();
            document.addPage(answerPage);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, answerPage)) {
                contentStream.setFont(font, 12); // フォントサイズを設定

                int yPosition = 750; // 初期Y座標

                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText(words.get(1).wordBook + " (解答)");

                contentStream.newLineAtOffset(0, -35); // 解答タイトルの下に行を移動

                for (int i = 0; i < words.size(); i++) {
                    contentStream.showText((i + 1) + ". " + words.get(i).getMeaning() + "　(" + words.get(i).getSection() + " No." + words.get(i).getNum() + ")");
                    contentStream.newLineAtOffset(0, -30); // 次の行のY位置を下げる
                }
                contentStream.endText(); // すべてのテキストを追加した後に呼び出す
            }

            // PDFをバイト配列に変換
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos); // PDFをByteArrayOutputStreamに保存

            // PDFをバイト配列リソースとして返す
            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

            // HTTPレスポンスのヘッダーを設定
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=" + words.get(1).wordBook + ".pdf");

            return new ResponseEntity<>(resource, headers, HttpStatus.OK); // PDFを返す

        } catch (IOException e) {
            // エラーハンドリング
            logger.error("PDF生成中にエラーが発生しました", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
