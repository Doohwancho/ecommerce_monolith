import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useQuery } from 'react-query';
import styled from 'styled-components'
import { ProductDetailResponseDTO, DiscountDTO } from 'model';

import ProductImages from './ProductImages';


interface GroupedProductItemOption {
  optionVariationName: string;
  quantity: number;
  price: number;
  discounts?: DiscountDTO[];
}

interface GroupedProductItems {
  productId: number;
  name: string;
  description: string;
  rating: number;
  ratingCount: number;
  categoryId: number;
  categoryName: string;
  categoryCode: string;
  [optionName: string]: GroupedProductItemOption | string | number | undefined;
}


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

const groupProductItems = (productItems: ProductDetailResponseDTO[]): GroupedProductItems => {
  const groupedProducts: GroupedProductItems = {};

  groupedProducts.productId = productItems[0].productId;
  groupedProducts.name = productItems[0].name;
  groupedProducts.description = productItems[0].description;
  groupedProducts.rating = productItems[0].rating;
  groupedProducts.ratingCount = productItems[0].ratingCount;
  groupedProducts.categoryId= productItems[0].categoryId;
  groupedProducts.categoryName = productItems[0].categoryName;
  groupedProducts.categoryCode = productItems[0].categoryCode;

  productItems.forEach(productItem=> {
    const optionName = `${productItem.optionName}`;

    if (!groupedProducts[optionName]) {
        groupedProducts[optionName] = {
          optionVariationName: productItem.optionVariationName,
          quantity: productItem.quantity,
          price: productItem.price,
          discounts: productItem.discounts
        };
    }
  });

  return groupedProducts;
}

const renderDiscounts = (discounts: DiscountDTO[]) => {
  return (
    <div className="discount-details">
      {discounts.map((discount, index) => (
        <div key={index}>
          <p>Discount Info</p>
          <p>Type: {discount.discountType}</p>
          <p>Value: {discount.discountValue}</p>
          <p>Start Date: {discount.startDate}</p>
          <p>End Date: {discount.endDate}</p>
        </div>
      ))}
    </div>
  );
};

const Product = () => {
  const { productId } = useParams();
  const [groupedProductItems, setGroupedProductItems] = useState<GroupedProductItems>({}); // New state for filtered products
  const [chosenOption, setChosenOption] = useState();

  const images = ["/images/category-product-image-1.png", "/images/category-product-image-2.png", "/images/category-product-image-3.png", "/images/category-product-image-4.png", "/images/category-product-image-5.png"];

  const { data: productsData, productIsLoading, productError } = useQuery<ProductDetailResponseDTO[], Error>(
    ['productId', productId],
    () => fetchProductsByProductId(productId),
  );

  //Q. why useEffect triggered 3 times?
  //1. initial render 때 component mount 될 때 실행됨
  //2. react-query가 fetch 실행할 때, productsData가 처음에 undefined or null이긴 하지만, productsData를 react-query가 건드리므로, 이 변수를 dependency로 받고있던 useEffect가 triggered 된다.
  //3. react-query가 끝나고, productsData가 undefined -> 값으로 찰 때, 3번째로 useEffect()가 실행된다.
  useEffect(() => {
    console.log("product.tsx's useEffect()-1 triggered!");
    console.log("productsData: ", productsData)
    
    if (productsData) {
      const grouptedProducts = groupProductItems(productsData);
      setGroupedProductItems(grouptedProducts);

      const firstOptionName = Object.keys(grouptedProducts).find(key => typeof grouptedProducts[key] === 'object');
      if (firstOptionName) {
        setChosenOption(grouptedProducts[firstOptionName] as GroupedProductItemOption);
      }
    }
    
  }, [productsData]); 

  const handleOptionChange = (optionName: string) => {
    setChosenOption(groupedProductItems[optionName] as GroupedProductItemOption);
  };

  console.log("-----------------------------");
  console.log("Product.tsx rendered!");

  if (productIsLoading) return <div>Loading...</div>;
  if (productError) return <div>Error: {productError.message}</div>;

  return (
    <>
      <Wrapper>
        <div className='section section-center page'>
          <div className='product-center'>
            <ProductImages images={images} />

            <section className='content'>
              <h2>{groupedProductItems.name}</h2>
              <h5 className='price'>{groupedProductItems.price}</h5>

              {groupedProductItems && (
                <>
                  <div className='info'>
                    <span>Description: </span>
                    {groupedProductItems.description}
                  </div>

                  <div className='info'>
                    <span>Category : </span>
                    {groupedProductItems.categoryName}
                  </div>
                </>
              )}

              <div className='info'>
                <span>Options : </span>
                <div>
                  {Object.keys(groupedProductItems).map(key => {
                    if (typeof groupedProductItems[key] === 'object') {
                      return (
                        <button key={key} onClick={() => handleOptionChange(key)}>
                          {key}
                        </button>
                      );
                    }
                    return null;
                  })}
                </div>
              </div>

              {chosenOption && (
                <>
                  <p>Price: {chosenOption.price}</p>
                  <p>Quantity: {chosenOption.quantity}</p>
                  {chosenOption.discounts && chosenOption.discounts.length > 0 ? (
                    <div>
                      {renderDiscounts(chosenOption.discounts)}
                    </div>
                  ) : (
                    <p>No discounts available for this option.</p>
                  )}
                </>
              )}
            </section>
          </div>
          <div className='empty-line'></div>
        </div>
      </Wrapper>
    </>
  );
};
const Wrapper = styled.main`
  .product-center {
    display: grid;
    gap: 4rem;
    margin-top: 2rem;

    @media (min-width: 992px) {
      grid-template-columns: 1fr 1fr;
      align-items: start;
    }
  }

  .content {
    background: #fff;
    padding: 2rem;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);

    h2 {
      color: var(--clr-primary-5);
      font-size: 2rem;
      margin-bottom: 1rem;
    }

    .price {
      color: var(--clr-primary-6);
      font-size: 1.5rem;
      font-weight: bold;
      margin-bottom: 1rem;
    }

    .info {
      display: flex;
      flex-direction: column;
      margin-bottom: 1rem;
      span {
        font-weight: bold;
      }
    }

    button {
      background: var(--clr-primary-5, #007bff); // Fallback color if variable not defined
      color: white;
      border: 1px solid transparent;
      padding: 0.75rem 1.5rem;
      border-radius: 5px;
      cursor: pointer;
      margin-right: 1rem;
      transition: background-color 0.3s ease;

      &:hover {
        background-color: var(--clr-primary-7, #0056b3); // Fallback color if variable not defined
      }

      &:not(:last-child) {
        margin-bottom: 1rem;
      }

      // Ensure visibility
      visibility: visible;
      opacity: 1;
    }

    .discount-details {
      margin-top: 1rem;
      padding: 1rem;
      background-color: #f7f7f7;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

      p {
        margin: 0.5rem 0;
        font-size: 0.9rem;
        color: #333;

        &:first-child {
          margin-top: 0;
        }
      }
    }
  }

  // Style for the empty line at the end
  .empty-line {
    height: 1rem; // Adjust the height as needed to create the desired space
  }

  hr {
    margin: 2rem 0;
  }

  @media (min-width: 992px) {
    .price {
      font-size: 1.75rem;
    }
  }
`

export default Product;
