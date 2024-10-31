package ma.alten.alten_backend.mapper;


import ma.alten.alten_backend.dto.ProductDto;
import ma.alten.alten_backend.model.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto toProductDto(Product product);

    Product toProduct(ProductDto productDto);

    List<ProductDto> toProductDtos(List<Product> products);

    List<Product> toProducts(List<ProductDto> productDTOs);

}
