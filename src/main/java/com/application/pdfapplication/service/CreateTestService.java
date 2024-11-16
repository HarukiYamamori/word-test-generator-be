package com.application.pdfapplication.service;

import com.application.pdfapplication.entity.Word;
import com.application.pdfapplication.model.GenerateTestRequest;
import com.application.pdfapplication.model.TestItem;
import com.application.pdfapplication.repository.WordRepository;
import org.apache.coyote.Response;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CreateTestService {

    private static final Logger logger = LoggerFactory.getLogger(CreateTestService.class);

    @Autowired
    private final WordRepository wordRepository;

    public CreateTestService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }


    public ResponseEntity<List<TestItem>> createTestItemService(GenerateTestRequest generateTestRequest) {
        String wordBook = generateTestRequest.getWordBook();
        List<String> sections = generateTestRequest.getSections();
        int wordNum = generateTestRequest.getWordNum();

        // データ取得
        List<Word> words = wordRepository.findWordsByBookAndSection(wordBook, sections, wordNum);

        // TestItemのリストを作成
        List<TestItem> testItemList = words.stream()
                .map(word -> {
                    TestItem item = new TestItem();
                    item.setWordBook(word.wordBook);
                    item.setWord(word.word);
                    item.setMeaning(word.meaning);
                    item.setSection(word.section);
                    item.setWordNum(word.num);
                    return item;
                })
                .collect(Collectors.toList());

        // 200 OKでレスポンスを返す
        return ResponseEntity.ok(testItemList);
    }

    public ResponseEntity<Resource> createTestService(GenerateTestRequest generateTestRequest) {
        String wordBook = generateTestRequest.getWordBook();
        List<String> sections = generateTestRequest.getSections();
        int wordNum = generateTestRequest.getWordNum();

        // データ取得
        List<Word> words = wordRepository.findWordsByBookAndSection(wordBook, sections, wordNum);

        if (words.isEmpty()) {
            logger.error("Word list is empty for the given criteria.");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        try (PDDocument document = new PDDocument()) {
            // フォントファイルをロード
            PDFont font = PDType0Font.load(document, getClass().getClassLoader().getResourceAsStream("font-file/MPLUS1p-Regular.ttf"), true);

            // --- 問題ページの生成 ---
            PDPage questionPage = new PDPage();
            document.addPage(questionPage);
            writeContentToPDF(document, questionPage, words, font, "問題", true);

            // --- 解答ページの生成 ---
            PDPage answerPage = new PDPage();
            document.addPage(answerPage);

            // 解答文の書き込み
            writeContentToPDF(document, answerPage, words, font, "解答", false);

            // PDFをバイト配列に変換
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos); // PDFをByteArrayOutputStreamに保存

            // PDFをバイト配列リソースとして返す
            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

            // HTTPレスポンスのヘッダーを設定
            HttpHeaders headers = new HttpHeaders();
            try {
                // ファイル名をUTF-8でエンコード
                String encodedFileName = URLEncoder.encode(wordBook, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20"); // スペースを%20に変換
                headers.add("Content-Disposition", "inline; filename=" + encodedFileName + ".pdf");
            } catch (UnsupportedEncodingException e) {
                logger.error("Error encoding file name", e);
            }

            return new ResponseEntity<>(resource, headers, HttpStatus.OK); // PDFを返す

        } catch (IOException e) {
            // エラーハンドリング
            logger.error("PDF生成中にエラーが発生しました", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ContentStreamの設定とテキスト描画処理を別メソッドに分割
    private PDPageContentStream createContentStream(PDDocument document, PDPage page, PDFont font, int fontSize) throws IOException {
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.setFont(font, fontSize); // フォントサイズを設定
        return contentStream;
    }

    // コンテンツ描画の共通処理
    private void writeContentToPDF(PDDocument document, PDPage page, List<Word> words, PDFont font, String title, boolean isQuestionPage) throws IOException {
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.setFont(font, 12);
            int yPosition = 750;

            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText(words.get(0).getWordBook() + " (" + title + ")");
            contentStream.newLineAtOffset(0, -35);

            for (Word word : words) {
                if (yPosition - (isQuestionPage ? 65 : 40) < 240) {
                    // 新しいページを作成
                    page = new PDPage();
                    document.addPage(page);
                    contentStream.close(); // 現在のストリームを閉じる
                    try (PDPageContentStream newContentStream = new PDPageContentStream(document, page)) {
                        newContentStream.setFont(font, 12);
                        yPosition = 750;
                        newContentStream.beginText();
                        newContentStream.newLineAtOffset(50, yPosition);
                    }
                } else {

                    // テキストを描画
                    contentStream.showText((words.indexOf(word) + 1) + ". ");
                    if (isQuestionPage) {
                        contentStream.showText(word.getWord());
                        contentStream.newLineAtOffset(0, -20);
                        contentStream.showText("____________________");
                    } else {
                        contentStream.showText(word.getMeaning() + " (" + word.getSection() + " No." + word.getNum() + ")");
                    }
                    contentStream.newLineAtOffset(0, -30);
                    yPosition -= isQuestionPage ? 35 : 10;
                }

                contentStream.endText();
            }
        }
    }
}
