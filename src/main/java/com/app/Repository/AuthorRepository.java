package com.app.Repository;

import com.app.Model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author,Long> {
    Author findByName(String name);
}
