package com.cho.ecommerce.domain.product.dto;

import java.util.List;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class PaginatedProductResponse {
    private List<ProductResponseDTO> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    
    public PaginatedProductResponse(Page<ProductResponseDTO> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}