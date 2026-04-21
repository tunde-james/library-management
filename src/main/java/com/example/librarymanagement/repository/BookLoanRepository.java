package com.example.librarymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.librarymanagement.entity.BookLoans;

public interface BookLoanRepository extends JpaRepository<BookLoans, Long> {

}
