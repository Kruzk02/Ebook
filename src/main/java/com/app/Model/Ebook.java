package com.app.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "ebooks")
public class Ebook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @JsonBackReference
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ebooks_authors",
            joinColumns = @JoinColumn(name = "ebooks_id"),
            inverseJoinColumns = @JoinColumn(name = "authors_id"))
    private Set<Author> authors;

    @ElementCollection
    private Set<Genre> genres;
    private String description;
    private String pdfUrl;
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "uploadBy",referencedColumnName = "id")
    private User uploadBy;

    @OneToMany(mappedBy = "ebooks")
    private Collection<Comment> comment;
}
