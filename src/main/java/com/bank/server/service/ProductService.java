package com.bank.server.service;

import com.bank.server.dto.ProductDTO;
import com.bank.server.dto.request.UpdateProductRequestDTO;
import com.bank.server.entity.Product;
import com.bank.server.enums.LogType;
import com.bank.server.enums.ProductCategory;
import com.bank.server.exception.ProductNotFoundException;
import com.bank.server.mapper.ProductMapper;
import com.bank.server.repository.ProductRepository;
import com.bank.server.utils.Generator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
            throw new ProductNotFoundException("PRODUCTS_NOT_FOUND","No Products found");
        }
        log.info("Fetched {} products successfully ",products.size());

        loggerService.log(
                "GET_ALL_PRODUCTS",
                "Fetched all products successfully",
                LogType.SUCCESS);

        return products;
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO request)
    {
        log.info("Creating product");
        Product product = productMapper.toEntity(request);
        product.setId(UUID.randomUUID().toString());
        product.setProductName(Generator.generateProductName());

        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with id {} ",savedProduct.getId());
        loggerService.log(
                "CREATE_PRODUCT",
                "Created product " + savedProduct.getProductName(),
                LogType.SUCCESS);

        return productMapper.toDTO(savedProduct);
    }


    public ProductDTO getProductById(String id) {

        log.info("Finding product with id {} ",id);
        Product product = productRepository.findById(id)
                .orElseThrow(()->new ProductNotFoundException("PRODUCT_NOT_FOUND","Product not found with this id : "+id));

        log.info("Product found with id {} ",id);

        loggerService.log(
                "GET_PRODUCT_BY_ID",
                "Fetched product for id "+id,
                LogType.SUCCESS);

        return productMapper.toDTO(product);
    }

    public List<ProductCategory> getProductCategories() {
        log.info("Fetching all product categories");

        List<ProductCategory> productCategories = List.of(ProductCategory.values());

        log.info("Fetched {} product categories", productCategories.size());

        loggerService.log(
                "GET_PRODUCT_CATEGORIES",
                "Fetched all products categories successfully",
                LogType.SUCCESS);

        return productCategories;
    }

    public List<ProductDTO> getAllProductsByCategory(ProductCategory category) {
        log.info("Fetching products for category {}",category);
        List<ProductDTO> allProducts = productRepository.findByCategory(category)
                .stream()
                .map(productMapper::toDTO)
                .toList();

        if(allProducts.isEmpty()){
            throw new ProductNotFoundException("PRODUCT_NOT_FOUND","No Products found for this category "+category);
        }

        log.info("Fetched {} products for category {}  successfully",allProducts.size(),category);

        loggerService.log(
                "GET_PRODUCTS_BY_CATEGORY",
                "Fetched all products for category "+category+ " successfully",
                LogType.SUCCESS);
        return allProducts;
    }

    @Transactional
    public ProductDTO updateProduct(String id,UpdateProductRequestDTO request)
    {
        log.info("Updating product with id {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(()->new ProductNotFoundException("PRODUCT_NOT_FOUND","Product not found with this id : "+id));

        if(request.getMinOperatingBalance()!=null){
            product.setMinOperatingBalance(request.getMinOperatingBalance());
        }

        if(request.getInterestRate()!=null){
            product.setInterestRate(request.getInterestRate());
        }

        if(request.getTermMonths()!=null){
            product.setTermMonths(request.getTermMonths());
        }

        Product updatedProduct = productRepository.save(product);

        log.info("Product updated successfully with id {}", id);

        loggerService.log(
                "UPDATE_PRODUCT",
                "Updated product with id " + id,
                LogType.SUCCESS
        );

        return productMapper.toDTO(updatedProduct);
    }
}
