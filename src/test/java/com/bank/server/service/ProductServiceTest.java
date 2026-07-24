package com.bank.server.service;

import com.bank.server.dto.ProductDTO;
import com.bank.server.dto.request.UpdateProductRequestDTO;
import com.bank.server.entity.Product;
import com.bank.server.enums.LogType;
import com.bank.server.enums.ProductCategory;
import com.bank.server.exception.ProductNotFoundException;
import com.bank.server.mapper.ProductMapper;
import com.bank.server.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private LoggerService loggerService;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldReturnAllProducts() {

        Product product = new Product();
        ProductDTO dto = new ProductDTO();

        when(productRepository.findAll())
                .thenReturn(List.of(product));

        when(productMapper.toDTO(product))
                .thenReturn(dto);

        List<ProductDTO> result = productService.getAllProducts();

        assertEquals(1, result.size());

        verify(productRepository).findAll();
        verify(productMapper).toDTO(product);

        verify(loggerService).log(
                eq("GET_ALL_PRODUCTS"),
                anyString(),
                eq(LogType.SUCCESS));
    }

    @Test
    void shouldThrowExceptionWhenNoProductsExist() {

        when(productRepository.findAll())
                .thenReturn(Collections.emptyList());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getAllProducts());

        verify(loggerService, never())
                .log(any(), any(), any());
    }

    @Test
    void shouldCreateProduct() {

        ProductDTO request = new ProductDTO();

        Product entity = new Product();

        Product saved = new Product();
        saved.setId("PROD001");

        ProductDTO response = new ProductDTO();
        response.setId("PROD001");

        when(productMapper.toEntity(request))
                .thenReturn(entity);

        when(productRepository.save(any(Product.class)))
                .thenReturn(saved);

        when(productMapper.toDTO(saved))
                .thenReturn(response);

        ProductDTO result =
                productService.createProduct(request);

        assertEquals("PROD001", result.getId());

        verify(productRepository).save(any(Product.class));

        verify(loggerService).log(
                eq("CREATE_PRODUCT"),
                anyString(),
                eq(LogType.SUCCESS));
    }

    @Test
    void shouldReturnProductById() {

        Product product = new Product();
        product.setId("PROD001");

        ProductDTO dto = new ProductDTO();
        dto.setId("PROD001");

        when(productRepository.findById("PROD001"))
                .thenReturn(Optional.of(product));

        when(productMapper.toDTO(product))
                .thenReturn(dto);

        ProductDTO result =
                productService.getProductById("PROD001");

        assertEquals("PROD001", result.getId());

        verify(productRepository).findById("PROD001");

        verify(loggerService).log(
                eq("GET_PRODUCT_BY_ID"),
                anyString(),
                eq(LogType.SUCCESS));
    }

    @Test
    void shouldThrowExceptionWhenProductIdNotFound() {

        when(productRepository.findById("PROD001"))
                .thenReturn(Optional.empty());

        ProductNotFoundException exception =
                assertThrows(
                        ProductNotFoundException.class,
                        () -> productService.getProductById("PROD001"));

        assertNotNull(exception.getMessage());
    }

    @Test
    void shouldReturnProductCategories() {

        List<ProductCategory> categories =
                productService.getProductCategories();

        assertNotNull(categories);

        assertEquals(
                ProductCategory.values().length,
                categories.size());

        verify(loggerService).log(
                eq("GET_PRODUCT_CATEGORIES"),
                anyString(),
                eq(LogType.SUCCESS));
    }

    @Test
    void shouldReturnProductsByCategory() {

        Product product = new Product();
        ProductDTO dto = new ProductDTO();


        when(productRepository.findByCategory(ProductCategory.SAVINGS))
                .thenReturn(Optional.of(product));


        when(productMapper.toDTO(product)).thenReturn(dto);

        List<ProductDTO> result = productService.getAllProductsByCategory(ProductCategory.SAVINGS);

        assertEquals(1, result.size());

        verify(productRepository)
                .findByCategory(ProductCategory.SAVINGS);

        verify(loggerService).log(
                eq("GET_PRODUCTS_BY_CATEGORY"),
                anyString(),
                eq(LogType.SUCCESS));
    }

    @Test
    void shouldThrowExceptionWhenCategoryHasNoProducts() {

        when(productRepository.findByCategory(ProductCategory.SAVINGS))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getAllProductsByCategory(
                        ProductCategory.SAVINGS));
    }

    @Test
    void shouldUpdateProduct() {

        Product product = new Product();
        product.setInterestRate(new BigDecimal("4"));
        product.setMinOperatingBalance(new BigDecimal("1000"));
        product.setTermMonths(12L);

        UpdateProductRequestDTO request =
                new UpdateProductRequestDTO();

        request.setInterestRate(new BigDecimal("5"));
        request.setMinOperatingBalance(new BigDecimal("2000"));
        request.setTermMonths(24L);

        ProductDTO dto = new ProductDTO();

        when(productRepository.findById("PROD001"))
                .thenReturn(Optional.of(product));

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        when(productMapper.toDTO(product))
                .thenReturn(dto);

        ProductDTO result =
                productService.updateProduct(
                        "PROD001",
                        request);

        assertNotNull(result);

        assertEquals(
                0,
                new BigDecimal("5")
                        .compareTo(product.getInterestRate()));

        assertEquals(
                new BigDecimal("2000"),
                product.getMinOperatingBalance());

        assertEquals(
                24L,
                product.getTermMonths());

        verify(productRepository).findById("PROD001");
        verify(productRepository).save(product);

        verify(loggerService).log(
                eq("UPDATE_PRODUCT"),
                anyString(),
                eq(LogType.SUCCESS));
    }

    @Test
    void shouldUpdateOnlyInterestRate() {

        Product product = new Product();
        product.setInterestRate(new BigDecimal("4"));
        product.setMinOperatingBalance(new BigDecimal("1000"));
        product.setTermMonths(12L);

        UpdateProductRequestDTO request =
                new UpdateProductRequestDTO();

        request.setInterestRate(new BigDecimal("5"));

        when(productRepository.findById("PROD001"))
                .thenReturn(Optional.of(product));

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        when(productMapper.toDTO(product))
                .thenReturn(new ProductDTO());

        productService.updateProduct("PROD001", request);

        assertEquals(
                0,
                new BigDecimal("5")
                        .compareTo(product.getInterestRate()));

        assertEquals(
                new BigDecimal("1000"),
                product.getMinOperatingBalance());

        assertEquals(
                12L,
                product.getTermMonths());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUnknownProduct() {

        UpdateProductRequestDTO request =
                new UpdateProductRequestDTO();

        when(productRepository.findById("PROD001"))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProduct(
                        "PROD001", request));
    }
}