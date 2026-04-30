package com.example.librarymanagement.repository;

import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.librarymanagement.entity.BookLoans;

public interface BookLoanRepository extends JpaRepository<BookLoans, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select bl from BookLoans bl where bl.id = :id")
    Optional<BookLoans> findByIdForUpdate(@Param("id") Long id);

    long countByUserIdAndIsReturnedFalse(Long userId);
}
