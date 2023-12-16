package com.cho.ecommerce.domain.product.controller;

import com.cho.ecommerce.api.ProductApi;
import com.cho.ecommerce.api.domain.PaginatedProductResponse;
import com.cho.ecommerce.api.domain.ProductDTO;
import com.cho.ecommerce.api.domain.ProductWithOptionsDTO;
import com.cho.ecommerce.api.domain.ProductWithOptionsListResponseDTO;
import com.cho.ecommerce.domain.product.adapter.ProductAdapter;
import com.cho.ecommerce.domain.product.mapper.ProductMapper;
import com.cho.ecommerce.domain.product.repository.CategoryRepository;
import com.cho.ecommerce.domain.product.repository.OptionRepository;
import com.cho.ecommerce.domain.product.repository.ProductRepository;
import com.cho.ecommerce.domain.product.service.ProductService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController implements ProductApi {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductAdapter productAdapter;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private OptionRepository optionRepository;

    

    
    @Override
    public ResponseEntity<PaginatedProductResponse> getProductsWithPagiation( //TODO - PaginatedProductResponse -> PaginatedProductResponseDTO 로 바꾸기
        Integer page,
        Integer size
    ) {
        PaginatedProductResponse productsWithPagination = productAdapter.getProductsWithPagination(
            page, size);
        
        return ResponseEntity.ok(productsWithPagination);
    }
    
    @Override
    public ResponseEntity<List<ProductDTO>> getTopTenHighestRatedProducts() {
        List<ProductDTO> top10RatedProducts = productAdapter.getTop10RatedProducts();
        return ResponseEntity.ok(top10RatedProducts);
    }
    
    @Override
    public ResponseEntity<List<com.cho.ecommerce.api.domain.ProductDetailResponseDTO>> getProductDetailDTOsById(Long id) {
        List<com.cho.ecommerce.api.domain.ProductDetailResponseDTO> productList = productAdapter.getProductDetailDTOsById(id);
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }
    
    @Override
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody com.cho.ecommerce.api.domain.ProductCreateRequestDTO product) {
        ProductDTO productDTO = productAdapter.saveProduct(product);
        return ResponseEntity.ok(productDTO);
    }
    
    @Override
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
        @Valid @RequestBody ProductDTO product) {
        ProductDTO productDTO = productAdapter.updateProduct(product);
        return ResponseEntity.ok(productDTO);
    }
    
    @Override
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    
    @Override
    public ResponseEntity<com.cho.ecommerce.api.domain.ProductWithOptionsListResponseDTO> getProductsWithOptionsByCategory(Long categoryId) {
        ProductWithOptionsListResponseDTO response = productAdapter.findAllProductsByCategory(
            categoryId);
    
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<List<com.cho.ecommerce.api.domain.AllCategoriesByDepthResponseDTO>> getAllCategoriesSortByDepth() {
        List<com.cho.ecommerce.api.domain.AllCategoriesByDepthResponseDTO> allCategoriesSortByDepth = categoryRepository.findAllCategoriesSortByDepth();
        //TODO - Q. 굳이 service layer안거치고 바로 controller가 repository에서 건져 올리는게 맞는걸까?
        //일단 이렇게 만들고, 만약 추후 저 코드를 재활용해야하는 상황이 오면,
        // 1. 서비스 레이어를 통해 호출하는 식으로 바꾸고,
        // 2. 메서드 input param type과 output type을 재사용성 높여야 하니까 adapter layer에서 타입 변환하는 식으로 만들고,
        // 3. 서비스 레이어에 있는 메서드를 재사용하자.
        return ResponseEntity.ok(allCategoriesSortByDepth);
    }
    
    @Override
    public ResponseEntity<List<com.cho.ecommerce.api.domain.OptionsOptionVariatonsResponseDTO>> getOptionsByCategory(Long categoryId) {
        List<com.cho.ecommerce.api.domain.OptionsOptionVariatonsResponseDTO> optionsAndOptionVariationsByCategoryId = optionRepository.findOptionsAndOptionVariationsByCategoryId(categoryId);
        //TODO - Q. 굳이 service layer안거치고 바로 controller가 repository에서 건져 올리는게 맞는걸까?
        //일단 이렇게 만들고, 만약 추후 저 코드를 재활용해야하는 상황이 오면,
        // 1. 서비스 레이어를 통해 호출하는 식으로 바꾸고,
        // 2. 메서드 input param type과 output type을 재사용성 높여야 하니까 adapter layer에서 타입 변환하는 식으로 만들고,
        // 3. 서비스 레이어에 있는 메서드를 재사용하자.
        return ResponseEntity.ok(optionsAndOptionVariationsByCategoryId);
    }
}
