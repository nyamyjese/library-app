package com.hei.school.entity;

import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_authors", uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "author_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "book_id", nullable = false)
    private UUID bookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;
}
