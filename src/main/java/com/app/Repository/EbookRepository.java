package com.app.Repository;

import com.app.Model.Author;
import com.app.Model.Ebook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EbookRepository extends JpaRepository<Ebook,Long> {
    Ebook findByAuthors(Author author);
}
