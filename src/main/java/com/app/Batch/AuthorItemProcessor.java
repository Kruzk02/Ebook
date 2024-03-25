package com.app.Batch;

import com.app.Model.Author;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemProcessor;

@Log4j2
public class AuthorItemProcessor implements ItemProcessor<Author,Author> {
    @Override
    public Author process(Author author) throws Exception {
        String name = author.getName().toLowerCase();

        Author transformedAuthor = new Author(name);
        log.info("Converting ( {} ) into ( {} )", author, transformedAuthor);
        return transformedAuthor;
    }
}
