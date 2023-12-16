import {ProductContainer, StyledLink, Card, ProductImage, ProductDetails, ProductCategory, ProductTitle, ProductDescription, ProductBottomDetails, ProductPrice } from '../styles/Category.styles';

const CategoryProducts = ({ productsData }) => {
    return (
        <ProductContainer>
            {
                Object.values(productsData).length > 0 ?
                Object.values(productsData).map((product) => (
                    <StyledLink to={`/products/${product.productId}`} key={`link-${product.productId}`}>
                        <Card key={product.productId}>
                        {/* source: https://codepen.io/mdshifut/pen/VrwBJq */}
                        {/* <Badge>Hot</Badge> */}
                        {/* <ProductThumb> */}
                            <ProductImage src="/images/category-product-image-1.webp" alt="" />
                        {/* </ProductThumb> */}
                        <ProductDetails>
                            <ProductCategory>{product.categoryName}</ProductCategory>
                            <ProductTitle>{product.name}</ProductTitle>
                            <ProductDescription>{product.description}</ProductDescription>
                            <ProductBottomDetails>
                                <ProductPrice>
                                    {/* <ProductPriceSmall>$96.00</ProductPriceSmall> */}
                                    {product.averagePrice} 원
                                </ProductPrice>
                            </ProductBottomDetails>
                        </ProductDetails>
                        </Card>
                    </StyledLink>
                )) : (
                    <p>No products available for this category.</p>
                )
            }
        </ProductContainer>
    );
};

export default CategoryProducts;
