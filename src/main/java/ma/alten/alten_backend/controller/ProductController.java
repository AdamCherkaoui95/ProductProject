package ma.alten.alten_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.alten.alten_backend.dto.ProductDto;
import ma.alten.alten_backend.exceptions.TechnicalException;
import ma.alten.alten_backend.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController // Annotation Spring indiquant que cette classe est un contrôleur REST
@RequestMapping("/api/products") // URL de base pour toutes les routes du contrôleur
@AllArgsConstructor // Injection des dépendances via Lombok
@Slf4j // Lombok pour la gestion des logs
public class ProductController {

    private final ProductService productService; // Bonne pratique : injection de dépendance

    @Operation(summary = "Create a new product", description = "Crée un nouveau produit")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDto> addProduct(@RequestPart("product") ProductDto productDto, @RequestPart("imageFile") MultipartFile imageFile) throws IOException {
        log.info("Adding new Product: {}", productDto); // Logging pour le suivi des opérations
        ProductDto createdProduct = productService.addProduct(productDto, imageFile); // Appel au service pour gérer la logique métier
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED); // Bonne pratique : Retourne 201 CREATED en cas de succès
    }

    @Operation(summary = "Retrieve all products", description = "Récupère la liste de tous les produits")
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) final String searchByCode,
            @RequestParam(required = false) final String searchByName,
            @RequestParam(required = false) final String searchByCategory,
            @RequestParam(required = false) final String searchByInventoryStatus,
            @RequestParam(required = false) final String searchByPriceRange
    ) {
        // Récupère une page de produits avec des critères de recherche
        Page<ProductDto> products = productService.getAllProducts(page, size, searchByCode, searchByName, searchByCategory, searchByInventoryStatus, searchByPriceRange);
        return ResponseEntity.ok(products); // Bonne pratique : Réponse avec 200 OK
    }

    @Operation(summary = "Update product details", description = "Mise à jour des détails d'un produit par ID")
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDTO) throws TechnicalException {
        log.info("Update product: {}", id); // Logging pour trace de l'ID mis à jour
        ProductDto updatedProduct = productService.updateProduct(id, productDTO); // Appel au service pour la mise à jour
        return ResponseEntity.ok(updatedProduct); // Bonne pratique : 200 OK pour la mise à jour réussie
    }

    @Operation(summary = "Retrieve product by ID", description = "Récupère les détails d'un produit par ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) throws TechnicalException {
        log.info("get product by id: {}", id); // Log de l'ID du produit récupéré
        return ResponseEntity.ok(productService.getProductById(id)); // Appel au service pour obtenir le produit
    }

    @Operation(summary = "Delete a product physical", description = "Suppression physique d'un produit par ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductPhysical(@PathVariable Long id) throws TechnicalException {
        log.info("delete product by id: {}", id); // Log de l'ID du produit supprimé
        productService.deleteProductPhysical(id); // Appel au service pour suppression
        return ResponseEntity.noContent().build(); // Bonne pratique : 204 No Content pour suppression réussie
    }

    @Operation(summary = "Archive a product", description = "Archivage logique d'un produit par ID")
    @DeleteMapping("archive/{id}")
    public ResponseEntity<Void> deleteProductLogical(@PathVariable Long id) throws TechnicalException {
        log.info("archive product by id: {}", id); // Log de l'ID du produit archivé
        productService.deleteProductLogical(id); // Appel au service pour suppression logique
        return ResponseEntity.noContent().build(); // 204 No Content pour l'archivage réussi
    }
}
