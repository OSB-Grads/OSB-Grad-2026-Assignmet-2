package com.bank.server.mapper;

import com.bank.server.dto.ProductDTO;
import com.bank.server.entity.Product;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public class ProductMapper {

//        ProductDTO toDTO(Product product);
//
//        Product toEntity (ProductDTO productDTO);

    public ProductDTO toDTO(Product product)
    {
        return ProductDTO.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .category(product.getCategory())
                .interestRate(product.getInterestRate())
                .minOperatingBalance(product.getMinOperatingBalance())
                .termMonths(product.getTermMonths())
                .build();
    }

    public Product toEntity (ProductDTO productDTO)
    {
        return Product.builder()
                .id(productDTO.getId())
                .productName(productDTO.getProductName())
                .category(productDTO.getCategory())
                .interestRate(productDTO.getInterestRate())
                .minOperatingBalance(productDTO.getMinOperatingBalance())
                .termMonths(productDTO.getTermMonths())
                .build();
    }

}
