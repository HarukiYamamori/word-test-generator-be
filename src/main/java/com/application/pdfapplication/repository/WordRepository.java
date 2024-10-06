package com.application.pdfapplication.repository;

import com.application.pdfapplication.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long> {

}
