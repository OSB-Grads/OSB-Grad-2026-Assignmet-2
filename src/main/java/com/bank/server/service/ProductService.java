package com.bank.server.service;

import com.bank.server.dto.ProductDTO;
import com.bank.server.entity.Product;
import com.bank.server.exception.ProductNotFoundException;
import com.bank.server.mapper.ProductMapper;
import com.bank.server.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final LoggerService loggerService;


    public List<ProductDTO> getAllProducts(){

        log.info("Fetching all products");
        List<ProductDTO> products = productRepository.findAll()
                .stream()
                .map(productMapper::toDTO)
                .toList();

        if(products.isEmpty()){
            throw new ProductNotFoundException("No Products found");
        }
        log.info("Fetch {} products successfully ",products.size());

        loggerService.log(
                "GET_ALL_PRODUCTS",
                "Fetched all products successfully",
                LogType.SUCCESS);

        return products;
    }

    public ProductDTO createProduct(ProductDTO request)
    {
        log.info("Creating product");
        Product product = productMapper.toEntity(request);
        product.setId(UuidGeneratorUtil.generateUuid());
        product.setProductName(ProductNameGenerator.generateProductName());

        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with id {} ",product.getId());
        loggerService.log(
                "CREATE_PRODUCT",
                "Created product " + savedProduct.getProductName(),
                LogType.SUCCESS);

        return productMapper.toDTO(savedProduct);
    }


    public ProductDTO getProductById(String id) {

        log.info("Finding product with id {} ",id);
        Product product = productRepository.findById(id)
                .orElseThrow(()->new ProductNotFoundException("Product not found with this id : "+id));

        log.info("Product Found with id {} ",id);

        loggerService.log(
                "GET_PRODUCT_BY_ID",
                "Fetched product for id "+id,
                LogType.SUCCESS);

        return productMapper.toDTO(product);
    }

    public List<String> getProductCategories() {
        log.info("Fetching all product categories");

        List<String> productCategories = productRepository.findAllCategories();

        if(productCategories.isEmpty()){
            throw new ProductNotFoundException("No product categories found");
        }

        loggerService.log(
                "GET_PRODUCT_CATEGORIES",
                "Fetched all products category successfully",
                LogType.SUCCESS);

        return productCategories;
    }

    public List<ProductDTO> getAllProductsByCategory(String category) {
        log.info("Fetching all products for {} category ",category);
        List<ProductDTO> allProducts = productRepository.findByCategory(category)
                .stream()
                .map(productMapper::toDTO)
                .toList();

        if(allProducts.isEmpty()){
            throw new ProductNotFoundException("No Products found for this category "+category);
        }

        log.info("Fetched {} products for category {}  successfully",allProducts.size(),category);

        loggerService.log(
                "PRODUCT_LIST_BY_CATEGORY",
                "Accessing all products based on category successfully",
                LogType.SUCCESS);
        return allProducts;
    }
}
