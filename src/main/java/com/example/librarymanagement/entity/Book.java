package com.example.librarymanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.SQLRestriction;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "books")
@Getter
@Setter
@SQLRestriction("deleted = false")
public class Book extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 150)
    private String author;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Book book = (Book) o;
        return getId() != null && getId().equals(book.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
