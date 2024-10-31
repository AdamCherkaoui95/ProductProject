package ma.alten.alten_backend.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Utilisation de Lombok pour le logging
import ma.alten.alten_backend.config.Messages;
import ma.alten.alten_backend.dto.ProductDto;
import ma.alten.alten_backend.enumeration.InventoryStatus;
import ma.alten.alten_backend.exceptions.TechnicalException;
import ma.alten.alten_backend.mapper.ProductMapper;
import ma.alten.alten_backend.model.Product;
import ma.alten.alten_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static ma.alten.alten_backend.util.constants.GlobalConstants.PRODUCT_NOT_FOUND;

@Service // Bonne pratique : Déclarer la classe en tant que service Spring
@AllArgsConstructor // Bonne pratique : Injection de dépendances via Lombok
@Slf4j // Bonne pratique : Logging via Lombok
public class ProductService {

    private final ProductRepository productRepository; // Injection de dépendance pour accès aux données
    private final ProductMapper productMapper; // Utilisation d’un mapper pour la conversion des entités/DTO
    private final EntityManager entityManager; // Utilisé pour des requêtes dynamiques avec Criteria
    private final Messages messages; // Pour les messages d’erreur centralisés

    Path imageStoragePath = Paths.get("product-images"); // Bonne pratique : Déclaration du chemin de stockage d'image

    // Constructeur pour initialisation et gestion du répertoire des images
    @Autowired
    public ProductService(ProductRepository productRepository, ProductMapper productMapper, EntityManager entityManager, Messages messages) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.entityManager = entityManager;
        this.messages = messages;

        // Bonne pratique : Gestion des fichiers avec création de répertoire si nécessaire
        try {
            Files.createDirectories(imageStoragePath);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la création du répertoire de stockage des images", e);
        }
    }

    @Transactional // Bonne pratique : Gestion des transactions pour  la cohérence
    public ProductDto addProduct(ProductDto productDTO, MultipartFile imageFile) throws IOException {
        log.debug("Start service add product"); // Logging des étapes du processus
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageFileName = saveImage(imageFile); // Sauvegarde d'image sécurisée avec UUID
            productDTO.setImage(imageFileName);
        }
        String code = generateCode(); // Génération d'un code unique pour chaque produit
        productDTO.setCode(code);
        Product product = productMapper.toProduct(productDTO); // Conversion DTO vers entité
        Product savedProduct = productRepository.save(product); // Sauvegarde en base
        log.debug("End service addProduct");
        return productMapper.toProductDto(savedProduct); // Conversion entité vers DTO
    }

    // Bonne pratique : Sauvegarde d'image avec nom unique
    private String saveImage(MultipartFile imageFile) throws IOException {
        String imageFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
        Path imagePath = imageStoragePath.resolve(imageFileName);
        Files.copy(imageFile.getInputStream(), imagePath);
        return imageFileName;
    }

    // Génération d'un code unique pour chaque produit
    private String generateCode() {
        String newCode;
        int nextCodeNumber;

        Optional<String> lastCodeOpt = productRepository.findTopByOrderByCodeDesc().map(Product::getCode);
        if (lastCodeOpt.isPresent()) {
            String lastCode = lastCodeOpt.get();
            nextCodeNumber = Integer.parseInt(lastCode.replaceAll("\\D+", "")) + 1;
        } else {
            nextCodeNumber = 1;
        }
        do {
            newCode = String.format("PRODUCT%03d", nextCodeNumber);
            nextCodeNumber++;
        } while (productRepository.existsByCode(newCode)); // Vérification de l’unicité du code

        return newCode;
    }

    // Méthode pour filtrer les produits en fonction de plusieurs critères
    public Page<ProductDto> getAllProducts(int page, int size, String searchByCode, String searchByName,
                                           String searchByCategory, String searchByInventoryStatus, String searchByPriceRange) {
        log.debug("Start service Get Products page: {} size: {} searchByCode: {} searchByName: {} searchByCategory: {} searchByInventoryStatus: {} searchByPriceRange: {}", page, size, searchByCode, searchByName, searchByCategory, searchByInventoryStatus, searchByPriceRange);
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;

        // Filtrage avancé via Criteria API en fonction des paramètres de recherche
        if (searchByCode != null || searchByName != null || searchByCategory != null ||
                searchByInventoryStatus != null || searchByPriceRange != null) {
            products = filterProducts(searchByCode, searchByName, searchByCategory, searchByInventoryStatus, searchByPriceRange, pageable);
        } else {
            products = productRepository.findAllWithDeletedIsFalse(pageable); // Exclusion des éléments supprimés
        }

        List<ProductDto> productDTOs = products.getContent().stream()
                .map(productMapper::toProductDto)
                .toList();
        log.debug("End service getProductsByCriteria ");
        return new PageImpl<>(productDTOs, pageable, products.getTotalElements());
    }

    // Méthode pour construire une requête dynamique avec Criteria
    private Page<Product> filterProducts(String searchByCode, String searchByName, String searchByCategory,
                                         String searchByInventoryStatus, String searchByPriceRange, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> root = criteriaQuery.from(Product.class);

        Predicate predicate = buildPredicate(criteriaBuilder, root, searchByCode, searchByName, searchByCategory, searchByInventoryStatus, searchByPriceRange);
        criteriaQuery.where(predicate);

        TypedQuery<Product> typedQuery = entityManager.createQuery(criteriaQuery);
        long totalCount = typedQuery.getResultList().size();
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Product> resultList = typedQuery.getResultList();

        return new PageImpl<>(resultList, pageable, totalCount); // Utilisation de PageImpl pour le paginage
    }

    // Construction dynamique des conditions de filtre
    private Predicate buildPredicate(CriteriaBuilder criteriaBuilder, Root<Product> root,
                                     String searchByCode, String searchByName, String searchByCategory,
                                     String searchByInventoryStatus, String searchByPriceRange) {
        Predicate predicate = criteriaBuilder.conjunction();

        if (searchByCode != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("code")),
                    "%" + searchByCode.toLowerCase() + "%"));
        }
        // Filtrage par nom
        if (searchByName != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                    "%" + searchByName.toLowerCase() + "%"));
        }
        // Filtrage par catégorie
        if (searchByCategory != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("category"), searchByCategory));
        }
        // Filtrage par statut d'inventaire
        if (searchByInventoryStatus != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("inventoryStatus"), searchByInventoryStatus));
        }
        // Filtrage par plage de prix
        if (searchByPriceRange != null) {
            String[] range = searchByPriceRange.split("-");
            double minPrice = Double.parseDouble(range[0]);
            double maxPrice = Double.parseDouble(range[1]);
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get("price"), minPrice, maxPrice));
        }
        return predicate;
    }

    // Gestion des exceptions en cas de produit non trouvé
    public ProductDto getProductById(Long id) throws TechnicalException {
        log.debug("Start service get product By Id {}", id);
        return productRepository.findById(id)
                .map(productMapper::toProductDto)
                .orElseThrow(() -> new TechnicalException(messages.get(PRODUCT_NOT_FOUND)));
    }

    // Suppression logique du produit
    public void deleteProductLogical(Long id) throws TechnicalException {
        log.debug("Start service delete logical product By Id {}", id);
        if (id == null) {
            throw new TechnicalException(messages.get(PRODUCT_NOT_FOUND));
        }
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new TechnicalException(messages.get(PRODUCT_NOT_FOUND));
        }
        Product product = productOptional.get();
        product.setDeleted(true); // Marquage comme supprimé
        productRepository.save(product);
    }
}
