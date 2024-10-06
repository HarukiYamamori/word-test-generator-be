package com.application.pdfapplication.util;

import com.application.pdfapplication.entity.Word;

public class AddData {
    public static Word addData(int num, String section, String wordBook, String word, String meaning) {
        Word scrapeDataList = new Word();
        scrapeDataList.num = num;
        scrapeDataList.wordBook = wordBook;
        scrapeDataList.section = section;
        scrapeDataList.word = word;
        scrapeDataList.meaning = meaning;

        return scrapeDataList;
    }
}
