package ma.alten.alten_backend.service;

import ma.alten.alten_backend.dto.ProductDto;
import ma.alten.alten_backend.enumeration.InventoryStatus;
import ma.alten.alten_backend.exceptions.TechnicalException;
import ma.alten.alten_backend.mapper.ProductMapper;
import ma.alten.alten_backend.model.Product;
import ma.alten.alten_backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    private ProductDto productDto;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Produit Test");
        productDto.setCategory("Category Test");
        productDto.setPrice(10.0);

        product = new Product();
        product.setId(1L);
        product.setName("Produit Test");
        product.setCategory("Category Test");
        product.setPrice(10.0);
    }

    @Test
    void addProduct_ShouldReturnProductDto() throws IOException {
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "image.jpg", "image/jpeg", new byte[0]);

        when(productMapper.toProduct(productDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toProductDto(product)).thenReturn(productDto);

        ProductDto result = productService.addProduct(productDto, imageFile);

        assertEquals(productDto, result);
        verify(productRepository).save(product);
    }

    @Test
    void getProductById_ShouldReturnProductDto() throws TechnicalException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toProductDto(product)).thenReturn(productDto);

        ProductDto result = productService.getProductById(1L);

        assertEquals(productDto, result);
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProductDto() throws TechnicalException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        when(productRepository.save(any(Product.class))).thenReturn(product);

        productDto.setInventoryStatus(String.valueOf(InventoryStatus.INSTOCK));

        when(productMapper.toProductDto(product)).thenReturn(productDto);

        ProductDto result = productService.updateProduct(1L, productDto);

        assertEquals(productDto, result);
        verify(productRepository).save(product);
    }


    @Test
    void deleteProductPhysical_ShouldNotThrowException() throws TechnicalException {
        doNothing().when(productRepository).deleteById(1L);

        assertDoesNotThrow(() -> productService.deleteProductPhysical(1L));
    }

    @Test
    void deleteProductLogical_ShouldNotThrowException() throws TechnicalException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product); // Simule le retour de la méthode save

        assertDoesNotThrow(() -> productService.deleteProductLogical(1L));
    }

}
