package mss301.se1911.group.assignment.restaurantservice.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.domain.entity.CategoryEntity;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.CategoryRepository;
import mss301.se1911.group.assignment.restaurantservice.infrastructure.persistence.JpaCategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final JpaCategoryRepository jpaCategoryRepository;

    @Override
    public CategoryEntity save(CategoryEntity category) {
        return jpaCategoryRepository.save(category);
    }

    @Override
    public Optional<CategoryEntity> findById(UUID id) {
        return jpaCategoryRepository.findById(id);
    }

    @Override
    public List<CategoryEntity> findByRestaurantId(UUID restaurantId) {
        return jpaCategoryRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaCategoryRepository.deleteById(id);
    }
}
