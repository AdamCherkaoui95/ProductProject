package ma.alten.alten_backend.controller;

import ma.alten.alten_backend.dto.ProductDto;
import ma.alten.alten_backend.exceptions.TechnicalException;
import ma.alten.alten_backend.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService; // Mocking du service pour isoler les tests du contrôleur

    @InjectMocks
    private ProductController productController; // InjectMocks pour injecter automatiquement les mocks dans le contrôleur

    private ProductDto productDto; // Objet DTO pour les tests

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialisation des mocks avant chaque test
        // Initialisation du produit pour les tests
        productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Produit Test");
        productDto.setCategory("Category Test");
        productDto.setPrice(10.0);
    }

    @Test
    void addProduct_ShouldReturnCreatedProduct() throws IOException {
        // Création d'un fichier d'image factice pour simuler l'upload
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "image.jpg", "image/jpeg", new byte[0]);

        // Configuration du mock pour retourner un productDto à partir du service
        when(productService.addProduct(any(ProductDto.class), any(MultipartFile.class))).thenReturn(productDto);

        // Exécution de la méthode addProduct
        ResponseEntity<ProductDto> response = productController.addProduct(productDto, imageFile);

        // Assertions : statut HTTP 201 CREATED et corps de la réponse est le produit créé
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(productDto, response.getBody());
    }

    @Test
    void getAllProducts_ShouldReturnProductPage() {
        // Création d'une page de produits factice avec un seul produit pour le test
        Page<ProductDto> productPage = new PageImpl<>(Collections.singletonList(productDto));
        when(productService.getAllProducts(0, 5, null, null, null, null, null)).thenReturn(productPage);

        // Exécution de la méthode getAllProducts
        ResponseEntity<Page<ProductDto>> response = productController.getAllProducts(0, 5, null, null, null, null, null);

        // Assertions : statut HTTP 200 OK et la page retournée est correcte
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productPage, response.getBody());
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() throws TechnicalException {
        when(productService.updateProduct(anyLong(), any(ProductDto.class))).thenReturn(productDto);

        ResponseEntity<ProductDto> response = productController.updateProduct(1L, productDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productDto, response.getBody());
    }

    @Test
    void getProductById_ShouldReturnProduct() throws TechnicalException {
        when(productService.getProductById(1L)).thenReturn(productDto);

        ResponseEntity<ProductDto> response = productController.getProductById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productDto, response.getBody());
    }

    @Test
    void deleteProductPhysical_ShouldReturnNoContent() throws TechnicalException {
        doNothing().when(productService).deleteProductPhysical(1L);

        ResponseEntity<Void> response = productController.deleteProductPhysical(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteProductLogical_ShouldReturnNoContent() throws TechnicalException {
        doNothing().when(productService).deleteProductLogical(1L);

        ResponseEntity<Void> response = productController.deleteProductLogical(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
