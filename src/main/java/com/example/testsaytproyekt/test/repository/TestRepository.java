package com.example.testsaytproyekt.test.repository;

import com.example.testsaytproyekt.test.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface TestRepository extends JpaRepository<Test, UUID>, JpaSpecificationExecutor<Test> {
}