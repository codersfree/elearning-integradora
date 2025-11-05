package com.example.codersfree.repository;

import com.example.codersfree.model.Cover;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CoverRepository extends JpaRepository<Cover, Long> {
    
    @Query("SELECT c FROM Cover c " +
           "WHERE c.active = true " +
           "AND c.startAt <= :today " +
           "AND (c.endAt IS NULL OR c.endAt >= :today)")
    List<Cover> findCurrentlyActiveCovers(LocalDate today);
}