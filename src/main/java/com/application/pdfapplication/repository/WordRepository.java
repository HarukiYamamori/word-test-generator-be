package com.application.pdfapplication.repository;

import com.application.pdfapplication.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {

    @Query(value = "SELECT DISTINCT word_book FROM words;", nativeQuery = true)
    List<String> findAllDistinctWordBooks();

    @Query(value = "SELECT section, COUNT(*) as word_count " +
            "FROM words " +
            "WHERE word_book = ?1 " +
            "GROUP BY section;", nativeQuery = true)
    List<Object[]> findSectionsByWordBook(String wordBook);

    @Query(value = "SELECT * FROM words WHERE word_book = ?1 AND section IN (?2) ORDER BY RAND() LIMIT ?3", nativeQuery = true)
    List<Word> findWordsByBookAndSection(String wordBook, List<String> sections, int limit);
}
