package com.app.Service.Impl;

import com.app.DTO.EBookDTO;
import com.app.Exceptions.EBookNotFoundException;
import com.app.Model.Author;
import com.app.Model.Ebook;
import com.app.Model.Genre;
import com.app.Repository.AuthorRepository;
import com.app.Repository.EbookRepository;
import com.app.Service.EbookService;
import com.app.Exceptions.AuthorNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * EBook Service class responsible for handling operation relates to EBook Entity. <p>
 * This class interacts with the EbookRepository for data access,
 * and utilizes ModelMapper for mapping between DTOs and entity object.
 */
@Service
@Log4j2
@EnableCaching
public class EBookServiceImpl implements EbookService {

    private final EbookRepository ebookRepository;
    private final AuthorRepository authorRepository;
    private final ModelMapper modelMapper;

    /**
     * Constructs a new EBookServiceImpl
     *
     * @param ebookRepository  The EbookRepository for accessing EBook relate data.
     * @param authorRepository The AuthorRepository for accessing Author relate data.
     * @param modelMapper      The ModelMapper for entity-DTO mapping.
     */
    @Autowired
    public EBookServiceImpl(EbookRepository ebookRepository, AuthorRepository authorRepository, RedisTemplate<String, Object> redisTemplate, ModelMapper modelMapper) {
        this.ebookRepository = ebookRepository;
        this.authorRepository = authorRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Retrieves all EBook.
     *
     * @return A List of all EBook.
     */
    @Cacheable(value = "ebooks")
    @Override
    public List<Ebook> getAllEBook() {
        return ebookRepository.findAll();
    }

    /**
     * Retrieves a EBook by its ID.
     *
     * @param id The ID of the EBook to retrieve.
     * @return The Ebook entity corresponding to the provided ID.
     */
    @Cacheable(value = "ebooks",key = "#id")
    @Override
    public Ebook getById(Long id) {
        return ebookRepository.findById(id).orElseThrow(() -> new EBookNotFoundException("EBook Not Found."));
    }

    /**
     * Retrieves a EBook by it Author.
     * @param author The Author of the Ebook to retrieve.
     * @return The Ebook entity corresponding to the provided Author.
     */
    @Override
    public Ebook getByAuthor(Author author) {
        return ebookRepository.findByAuthors(author);
    }

    /**
     * Saves a new EBook with a provided EBookDTO and MultipartFile. <p>
     * Saves the upload file to the "upload" directory and sets the pdf url and file name to the EBook.
     *
     * @param eBookDTO The EBookDTO object containing EBook information.
     * @param multipartFile The MultipartFile containing the upload pdf file.
     * @return The saved EBook entity.
     * @throws IOException If an I/O error occurs while saving the file.
     */

    @Override
    public Ebook save(EBookDTO eBookDTO, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get("upload");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            Ebook ebook = modelMapper.map(eBookDTO, Ebook.class);
            ebook.setPdfUrl(filePath.toString());
            ebook.setFileName(multipartFile.getOriginalFilename());

            Set<Author> authors = new HashSet<>();
            for (String authorName : eBookDTO.getAuthors()) {
                Author author = authorRepository.findByName(authorName);
                if (author == null) {
                    throw new AuthorNotFoundException("Author not found: " + authorName);
                }
                authors.add(author);
            }

            Set<Genre> genres = new HashSet<>();
            for (String genreString : eBookDTO.getGenres()) {
                genres.add(Genre.valueOf(genreString.toUpperCase()));
            }

            ebook.setAuthors(authors);
            ebook.setGenres(genres);

            return ebookRepository.save(ebook);
        } catch (IOException e) {
            log.error("IO Error occurred while saving file: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while saving eBook: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Deletes a EBook By its ID, if it exists.
     *
     * @param id The ID of the EBook to delete.
     */
    @Override
    public void deleteById(Long id) {
        ebookRepository.findById(id).ifPresent(ebook -> ebookRepository.deleteById(id));
    }
}
