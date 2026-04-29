package com.example.librarymanagement.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.SQLRestriction;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "book_loans",
        indexes = { @Index(name = "idx_book_loans_user_id", columnList = "user_id"),
                @Index(name = "idx_book_loans_book_id", columnList = "book_id"),
                @Index(name = "idx_book_loans_is_returned", columnList = "is_returned") })
@Getter
@Setter
@SQLRestriction("deleted = false")
public class BookLoans extends BaseEntity {

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "is_returned", nullable = false)
    private Boolean isReturned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;
        
        BookLoans bookLoans = (BookLoans) o;
        return getId() != null && getId().equals(bookLoans.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
