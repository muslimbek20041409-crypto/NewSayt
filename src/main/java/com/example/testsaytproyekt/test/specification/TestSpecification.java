package com.example.testsaytproyekt.test.specification;

import com.example.testsaytproyekt.test.entity.Test;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TestSpecification {

    public static Specification<Test> hasTeacherId(UUID teacherId) {
        return (root, query, cb) ->
                teacherId == null ? null :
                        cb.equal(root.get("teacher").get("id"), teacherId);
    }

    public static Specification<Test> titleContains(String keyword) {
        return (root, query, cb) ->
                (keyword == null || keyword.isBlank()) ? null :
                        cb.like(
                                cb.lower(root.get("title")),
                                "%" + keyword.toLowerCase() + "%"
                        );
    }
}