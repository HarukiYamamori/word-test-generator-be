package com.application.pdfapplication.service;

import com.application.pdfapplication.entity.Word;
import com.application.pdfapplication.repository.WordRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.application.pdfapplication.util.AddData.addData;
import static com.application.pdfapplication.util.SubString.getSubString;

@Service
public class ScrapeService {

    @Autowired
    private final WordRepository wordRepository;

    public final List<Word> data = new ArrayList<>();
    private String version;
    private static final String COMMA = ",";
    private static final String ZENKAKU_COMMA = "、";
    private static final String WORD_LIST = "単語一覧";
    private static final String IDIOM_LIST = "熟語一覧";

    public ScrapeService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    public ResponseEntity<Void> scrapeWordService(String url, String wordNumHeader, String wordHeader, String wordMeaningHeader) {
        try {
            data.clear();
            List<Word> wordList = scrapeWord(url, wordNumHeader, wordHeader, wordMeaningHeader);
            wordRepository.saveAll(wordList);

            return ResponseEntity.ok().build();
        } catch (IOException e) {
            // 例外をログに記録（ロギングフレームワークを使用すると良い）
            System.err.println("IOException occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (NumberFormatException e) {
            // 数値変換エラーをキャッチ
            System.err.println("Number format error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public List<Word> scrapeWord(String url, String wordNumHeader, String wordHeader, String wordMeaningHeader) throws IOException {
        Document document = Jsoup.connect(url).get();

        version = document.getElementsByClass("entry-title").text();
        if(version.contains(IDIOM_LIST)) {
            version = getSubString(version, "", IDIOM_LIST).trim();
        } else if (version.contains(WORD_LIST)){
            version = getSubString(version, "", WORD_LIST).trim();
        }

        Elements rows = document.select(".row-hover > tr");
        for(Element row : rows) {
            String word = replaceComma(row.getElementsByClass(getTargetClassNm(document, wordHeader)).text());
            String wordMeaning = replaceComma(row.getElementsByClass(getTargetClassNm(document, wordMeaningHeader)).text());
            int wordNum = Integer.parseInt(row.getElementsByClass(getTargetClassNm(document, wordNumHeader)).text());
            String section = parseNumToSection(wordNum);

            Word scrapeDataList = addData(wordNum, version, section, word, wordMeaning);
            data.add(scrapeDataList);
        }

        return data;
    }

    private static String parseNumToSection(int num) {
        String section;
        int sectionNum = (num - 1) / 100 + 1;
        if(sectionNum <= 0) {
            return null;
        } else {
            section = "section" + sectionNum;
            return section;
        }
    }

    private String replaceComma(String str) {
        if(str.contains(COMMA)) {
            str = str.replace(COMMA, ZENKAKU_COMMA);
        }
        return str;
    }

    private String getTargetClassNm(Document document, String str) {
        Elements headerRow = document.select("thead th");
        for(Element headerItem : headerRow) {
            String rowStr = headerItem.text();
            if(str.equals(rowStr)) {
                return headerItem.className().replace("sorting", "").trim();
            }
        }

        return null;
    }
}
