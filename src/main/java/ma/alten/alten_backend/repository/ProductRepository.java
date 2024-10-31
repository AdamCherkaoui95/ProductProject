package ma.alten.alten_backend.repository;

import ma.alten.alten_backend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findTopByOrderByCodeDesc();
    boolean existsByCode(String code);

    @Query("SELECT p FROM Product p WHERE p.deleted = false OR p.deleted IS NULL ORDER BY p.updatedAt DESC")
    Page<Product> findAllWithDeletedIsFalse(Pageable pageable);

}
