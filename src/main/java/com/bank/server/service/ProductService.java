package com.bank.server.service;

import com.bank.server.dto.ProductDTO;
import com.bank.server.entity.Product;
import com.bank.server.exception.ProductNotFoundException;
import com.bank.server.mapper.ProductMapper;
import com.bank.server.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    public List<ProductDTO> getAllProducts(){
        List<ProductDTO> products = productRepository.findAll()
                .stream()
                .map(productMapper::toDTO)
                .toList();

        if(products.isEmpty()){
            throw new ProductNotFoundException("No Products found");
        }

        return products;
    }

    public ProductDTO createProduct(ProductDTO request)
    {
        Product product = productMapper.toEntity(request);
        product.setId(UuidGeneratorUtil.generateUuid());
        product.setProductName(ProductNameGenerator.generateProductName());

        Product savedProduct = productRepository.save(product);

        return productMapper.toDTO(savedProduct);
    }


    public ProductDTO getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()->new ProductNotFoundException("Product not found with this id : "+id));
        return productMapper.toDTO(product);
    }

    public List<String> getProductCategories() {
        return productRepository.findAllCategories();
    }

    public List<ProductDTO> getAllProductsByCategory(String category) throws SQLException {
        List<ProductDTO> allProducts = productRepository.findProductByCategory(category)
                .stream()
                .map(productMapper::toDTO)
                .toList();

        if(allProducts.isEmpty()){
            throw new ProductNotFoundException("No Products found for this category "+category);
        }

        loggerService.log("PRODUCT_LIST_BY_CATEGORY","Accessing all products based on product category successfull", LogType.SUCCESS);
        return allProducts;
    }
}
