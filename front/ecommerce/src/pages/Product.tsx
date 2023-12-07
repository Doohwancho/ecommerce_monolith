import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from 'react-query';
import { useRecoilValue, useSetRecoilState, useRecoilState, useRecoilValueLoadable } from 'recoil';
import { categoriesState } from '../store/state'; // Adjust the import path as needed
import { AllCategoriesByDepthResponseDTO, ProductDetailResponseDTO, DiscountDTO } from 'model';


const fetchProductsByProductId = async (productId: number): Promise<ProductDetailResponseDTO[]> => {
  const baseUrl = 'http://127.0.0.1:8080';
  const endpoint = `/products/${productId}`;
  const fullUrl = baseUrl + endpoint;

  const response = await fetch(fullUrl, { credentials: 'include' });
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  return response.json();
};  

const Product = () => {
  const [categories, setCategories] = useRecoilState(categoriesState);

  const { data: productsData, productIsLoading, productError } = useQuery<ProductDetailResponseDTO[], Error>(
    ['productId', 3],
    () => fetchProductsByProductId(3),
  );

  useEffect(() => {
    console.log("Updated categories:", categories);
    console.log("updated products by categoryId", productsData);
  }, [categories, productsData]); 

  if (productIsLoading) return <div>Loading...</div>;
  if (productError) return <div>Error: {productError.message}</div>;

  return (
    <div>
        <h1>productDetails</h1>

        {productsData && productsData.length > 0 ? (
          <ul>
            {productsData.map((product: ProductDetailResponseDTO, index: number) => (
              <li key={index}>
                productId: {product.productId},
                product name: {product.name},
                product description: {product.description},
                product rating: {product.rating},
                product rating count: {product.ratingCount},
                product quantiy: {product.quantity},
                product price: {product.price},
                category name: {product.categoryName}
                category code: {product.categoryCode}
                option name: {product.optionName}
                option variation name: {product.optionVariationName}
                {product.discounts && product.discounts.length > 0 && (
                  <>
                    discount type: {product.discounts[0].discountType},
                    discount value: {product.discounts[0].discountValue}
                  </>
                )}
              </li>
            ))}
          </ul>
        ) : (
          <p>No product available.</p>
        )}
    </div>
  );
};

export default Product;
