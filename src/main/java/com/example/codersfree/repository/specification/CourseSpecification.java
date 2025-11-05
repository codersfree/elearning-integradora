package com.example.codersfree.repository.specification;

import com.example.codersfree.model.Course;
import com.example.codersfree.enums.CourseStatus;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.JoinType;

import java.util.List;

public class CourseSpecification {

    public static Specification<Course> hasStatus(CourseStatus status) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("status"), status);
    }
    
    public static Specification<Course> nameContains(String name) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Course> inCategories(List<Long> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            // FIX: Comprobamos si query es nulo antes de usarlo
            if (query != null) {
                query.distinct(true); // Evita duplicados
            }
            // Hacemos "join" con la tabla de categorías
            return root.join("category", JoinType.INNER).get("id").in(categoryIds);
        };
    }

    public static Specification<Course> inLevels(List<Long> levelIds) {
        return (root, query, criteriaBuilder) -> {
            // FIX: Comprobamos si query es nulo antes de usarlo
            if (query != null) {
                query.distinct(true);
            }
            return root.join("level", JoinType.INNER).get("id").in(levelIds);
        };
    }

    public static Specification<Course> inPrices(List<Long> priceIds) {
        return (root, query, criteriaBuilder) -> {
            // FIX: Comprobamos si query es nulo antes de usarlo
            if (query != null) {
                query.distinct(true);
            }
            return root.join("price", JoinType.INNER).get("id").in(priceIds);
        };
    }
}

