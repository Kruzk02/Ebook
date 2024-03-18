package com.app.Service.Impl;

import com.app.DTO.EBookDTO;
import com.app.Model.Author;
import com.app.Model.Ebook;
import com.app.Repository.EbookRepository;
import com.app.Service.EbookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * EBook Service class responsible for handling operation relates to EBook Entity. <p>
 * This class interacts with the EbookRepository for data access,
 * and utilizes ModelMapper for mapping between DTOs and entity object.
 */
@Service
public class EBookServiceImpl implements EbookService {

    private final EbookRepository ebookRepository;
    private final ModelMapper modelMapper;

    /**
     * Constructs a new EBookServiceImpl
     *
     * @param ebookRepository The EbookRepository for accessing EBook relate data.
     * @param modelMapper The ModelMapper for entity-DTO mapping.
     */
    @Autowired
    public EBookServiceImpl(EbookRepository ebookRepository, ModelMapper modelMapper) {
        this.ebookRepository = ebookRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Retrieves all EBook.
     *
     * @return A List of all EBook.
     */
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
    @Override
    public Ebook getById(Long id) {
        return ebookRepository.findById(id).orElse(null);
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
        if(!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        try (InputStream inputStream = multipartFile.getInputStream()){
            Path filePath = uploadPath.resolve(multipartFile.getOriginalFilename());
            Files.copy(inputStream,filePath, StandardCopyOption.REPLACE_EXISTING);

            Ebook ebook = modelMapper.map(eBookDTO,Ebook.class);
            ebook.setPdfUrl(filePath.toString());
            ebook.setFileName(multipartFile.getOriginalFilename());

            return ebookRepository.save(ebook);
        }catch (Exception e){
            throw new IOException("Could not save file: " + multipartFile.getOriginalFilename(), e);
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
