package com.app.Service.Impl;

import com.app.DTO.AuthorDTO;
import com.app.Model.Author;
import com.app.Repository.AuthorRepository;
import com.app.Service.AuthorService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author Service class responsible for handling operation to Author entity.
 * This class interacts with the AuthorRepository for data access,
 * and utilizes ModelMapper for mapping between DTOs and entity object.
 */
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final ModelMapper modelMapper;

    /**
     * Constructs a new AuthorServiceImpl.
     * @param authorRepository The AuthorRepository for accessing Author relate data.
     * @param modelMapper The ModelMapper for entity-DTO mapping.
     */
    public AuthorServiceImpl(AuthorRepository authorRepository, ModelMapper modelMapper) {
        this.authorRepository = authorRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Retrieves all Author.
     * @return A list of all Author.
     */
    @Override
    public List<Author> getAllAuthor() {
        return authorRepository.findAll();
    }

    /**
     * Retrieves Author by its Name.
     * @param name The Name of the Author to retrieve.
     * @return The Author entity corresponding to the provided name.
     */
    @Override
    public Author getByName(String name) {
        return authorRepository.findByName(name);
    }

    /**
     * Retrieves Author by its ID.
     * @param id The ID of the Author to retrieve.
     * @return The Author entity corresponding to the provided ID.
     */
    @Override
    public Author getById(Long id) {
        return authorRepository.findById(id).orElse(null);
    }

    /**
     * Saves a new Author with provided AuthorDTO.
     * @param authorDTO The AuthorDTO object containing Author information.
     * @return The saved Author entity.
     */
    @Override
    public Author save(AuthorDTO authorDTO) {
        Author author = modelMapper.map(authorDTO, Author.class);
        return authorRepository.save(author);
    }

    /**
     * Delete Author by its ID, if it exists.
     * @param id The ID of the Author to delete.
     */
    @Override
    public void deleteById(Long id) {
        authorRepository.findById(id).ifPresent(author -> authorRepository.deleteById(id));
    }
}
