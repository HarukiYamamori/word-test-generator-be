package com.application.pdfapplication.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "words")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "num")
    public int num;
    @Column(name = "word_book")
    public String wordBook;
    @Column(name = "section")
    public String section;
    @Column(name = "word")
    public String word;

    @Column(columnDefinition = "TEXT", name = "meaning")
    public String meaning;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getWordBook() {
        return wordBook;
    }

    public void setWordBook(String wordBook) {
        this.wordBook = wordBook;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
