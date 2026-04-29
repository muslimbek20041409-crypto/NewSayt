package com.example.testsaytproyekt.result.repository;

import com.example.testsaytproyekt.result.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ResultRepository extends JpaRepository<Result, UUID> {
    List<Result> findByStudentId(UUID id);

    List<Result> findByTestId(UUID testId);

        List<Result> findByTestTeacherId(UUID teacherId);

}
