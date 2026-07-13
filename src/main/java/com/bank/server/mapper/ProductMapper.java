package com.bank.server.mapper;

import com.bank.server.dto.ProductDTO;
import com.bank.server.entity.Product;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ProductMapper {

        ProductDTO toDTO(Product product);

        Product toEntity (ProductDTO productDTO);

}
