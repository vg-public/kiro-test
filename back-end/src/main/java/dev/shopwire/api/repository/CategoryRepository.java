package dev.shopwire.api.repository;

import dev.shopwire.api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByParentIsNullAndActiveTrue();
}
