package com.bank.server.controller;

import com.bank.server.dto.ProductDTO;
import com.bank.server.dto.request.UpdateProductRequestDTO;
import com.bank.server.entity.Product;
import com.bank.server.enums.ProductCategory;
import com.bank.server.service.ProductService;
import jakarta.validation.Valid;
import org.hibernate.sql.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService)
    {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDto)
    {
        ProductDTO savedProduct = productService.createProduct(productDto);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts()
    {
        List<ProductDTO> allProducts = productService.getAllProducts();
        return ResponseEntity.ok(allProducts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable  String id)
    {
        ProductDTO productById = productService.getProductById(id);
        return ResponseEntity.ok(productById);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ProductCategory>> getAllProductCategories()
    {
        List<ProductCategory> productCategories = productService.getProductCategories();
        return ResponseEntity.ok(productCategories);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable String id,@Valid @RequestBody UpdateProductRequestDTO request)
    {
        ProductDTO updatedProduct = productService.updateProduct(id,request);
        return ResponseEntity.ok(updatedProduct);
    }

}
