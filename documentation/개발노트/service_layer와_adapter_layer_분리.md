### 1. 문제
```java
@Service
public class ProductService {

    private ProductService self;

    @Transactional
    public List<Product> getProductDetailDTOsById(Long productId) {
        //business logics
    }

    public List<ProductDetailResponseDTO> findProductDetailDTOsById(Long productId) {
        List<Product> productDetailDTOsList = self.getProductDetailDTOsById(productId); //fix: solution to "Methods should not call same-class methods with incompatible @Transactional"

        return productMapper.productsToProductDetailDTOs(productDetailDTOsList);
    }
}
```
1. 서비스 메서드의 반환타입이 `List<ResponseDTO>`로 하자니, 재사용성이 떨어지고, `List<도메인VO>`로 하면 재사용성은 올라가는데, 컨트롤러에 반환시 ResponseDTO로 한번 더 변환해주어야 한다.
2. ResponseDTO로 변환해주는 메서드를 동일한 서비스 레이어 파일에서 작성 시, @Transactional이 걸려있는 경우, self.메서드()로 참조해야 하는데, 그닥 좋은 패턴은 아닌 듯 하다.
3. 서비스 레이어에서는 서비스 로직 관련 코드만 있어야 하는데, 로직은 없고 DTO 변환 코드가 있어서 가독성에 문제가 생기고 서비스 레이어가 비대해진다.


### 2. 해결책

service layer와 adapter layer를 분리한다.

```java
1) Adapter layer

@Component
public class ProductAdapter {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductService productService;

    public List<ProductDetailResponseDTO> getProductDetailDTOsById(Long id) {
        List<Product> productList = productService.getProductDetailDTOsById(id);

        return productMapper.productsToProductDetailDTOs(productList);
    }
}
```

```java
2) Service layer

@Service
public class ProductService {

    private ProductService self;

    @Transactional
    public List<Product> getProductDetailDTOsById(Long productId) {
        //business logics
    }
}
```

1. 타입 변환만 전문적으로 하는 어답터 레이어
2. 서비스 레이어

...로 분리함으로써, 서비스 레이어에서는 비즈니스 로직만 있도록 했다.

