import React, { useEffect } from 'react';
import { useQuery } from 'react-query';
import { OptionsOptionVariationsResponseDTO, ProductWithOptionsListResponseDTO, ProductWithOptionsDTO } from 'model';
import { useNavigate, useParams } from 'react-router-dom';
import styled from 'styled-components';

import Header from '../components/common/Header'
import TopNav from '../components/common/TopNav/TopNav';
import Footer from '../components/common/Footer'

interface GroupedOptions {
  categoryId: number;
  optionId: number;
  optionName: string;
  optionVariationNames: string[];
}

const fetchCategoryOptions = async (categoryId: number): Promise<OptionsOptionVariationsResponseDTO[]> => {
    const baseUrl = 'http://127.0.0.1:8080';
    const endpoint = `/categories/${categoryId}/options`;
    const fullUrl = baseUrl + endpoint;

    const response = await fetch(fullUrl, { credentials: 'include' });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  };  

  const fetchProductsWithOptionsByCategoryId = async (categoryId: number): Promise<ProductWithOptionsListResponseDTO> => {
    const baseUrl = 'http://127.0.0.1:8080';
    const endpoint = `/products/category/${categoryId}`;
    const fullUrl = baseUrl + endpoint;

    const response = await fetch(fullUrl, { credentials: 'include' });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  };  

  const groupOptionsByOptionId = (data: OptionsOptionVariationsResponseDTO[]): GroupedOptions[] => {
    const grouped: Record<number, GroupedOptions> = {};

    data.forEach(item => {
        const { categoryId, optionId, optionName, optionVariationName } = item;
        if (!grouped[optionId]) {
            grouped[optionId] = { categoryId, optionId, optionName, optionVariationNames: [] };
        }
        grouped[optionId].optionVariationNames.push(optionVariationName);
    });

    return Object.values(grouped);
};


const Category = () => {
  const navigate = useNavigate();
  const { lowCategoryId } = useParams();
  const { data: optionsData, isLoading: optionIsLoading, error: optionError, refetch: refetchOptions } = useQuery<OptionsOptionVariationsResponseDTO[], Error>(
    ['categoryOptions', lowCategoryId], //react-query의 키. lowCategoryId가 바뀌면 refetch한다. 
    () => fetchCategoryOptions(lowCategoryId),
    {
      enabled: !!lowCategoryId, // Enable query only if categoryId is available
    }
  );

  const { data: productsData, isLoading: productIsLoading, error: productError,refetch: refetchProducts  } = useQuery<ProductListResponseDTO, Error>(
    ['categoryId', lowCategoryId], //react-query의 키. lowCategoryId가 바뀌면 refetch한다. 
    () => fetchProductsWithOptionsByCategoryId(lowCategoryId),
    {
      enabled: !!lowCategoryId, // Enable query only if categoryId is available
    }
  );

  useEffect(() => {
    if (lowCategoryId) {
      refetchOptions();
      refetchProducts();
    }
    console.log("updated option datas:", optionsData);
    if (optionsData) {
      console.log("grouped option data", groupOptionsByOptionId(optionsData));
    }
    console.log("updated products by categoryId", productsData);
  }, [lowCategoryId, optionsData, productsData]); 

  const onProductClick = (productId: number) => {
    navigate(`/product/${productId}`);
  };

  return (
    <>
      <Header />
      <TopNav />
      <MainContainer>
        <Area className="left-area">
          {/* option filters */}
          {optionsData && groupOptionsByOptionId(optionsData)?.map(group => (
              <Container key={group.optionId}>
                  <Title>{group.optionName}</Title>
                  <CheckboxList>
                    {group.optionVariationNames.map((variation, variationIndex) => (
                      <CheckboxItem key={`${group.optionId}-${variationIndex}`}>
                          <input type="checkbox" />
                          <label>
                              {variation}
                          </label>
                      </CheckboxItem>
                    ))}
                  </CheckboxList>
              </Container>
            ))}
          {/* price filter */}
          <Container key="price-key">
            <Title>Price</Title>
            <CheckboxList>
              <CheckboxItem>
                  <input type="checkbox" />
                  <label>
                      0 - 50,000원
                  </label>
              </CheckboxItem>
            </CheckboxList>
            <CheckboxList>
              <CheckboxItem>
                  <input type="checkbox" />
                  <label>
                      50,000 - 100,000원
                  </label>
              </CheckboxItem>
            </CheckboxList>
            <CheckboxList>
              <CheckboxItem>
                  <input type="checkbox" />
                  <label>
                    100,000 - 150,000원
                  </label>
              </CheckboxItem>
            </CheckboxList>
            <CheckboxList>
              <CheckboxItem>
                  <input type="checkbox" />
                  <label>
                    150,000 - 200,000원
                  </label>
              </CheckboxItem>
            </CheckboxList>
            <CheckboxList>
              <CheckboxItem>
                  <input type="checkbox" />
                  <label>
                      200,000원 +
                  </label>
              </CheckboxItem>
            </CheckboxList>
          </Container>
        </Area>
        <Area className="right-area">
          <div>
              <h1>Product List that belongs to categoryId</h1>

              {productsData && productsData.products.length > 0 ? (
                  <ul>
                  {productsData.products.map((product: ProductWithOptionsDTO, index: number) => (
                      <li key={index} onClick={() => onProductClick(product.productId)}>
                          c.id: {lowCategoryId} p.name: {product.name}, option: {product.optionName}, optionVariation: {product.optionVariationName} quantity: {product.quantity} price: {product.price}
                      </li>
                  ))}
                  </ul>
              ) : (
                  <p>No products available for this category.</p>
              )}
          </div>
        </Area>
      </MainContainer>
      <Footer />
    </>
  );
};

const MainContainer = styled.div`
    display: flex;
    width: 100%;
`;

const Area = styled.div`
    flex: ${props => props.className === 'left-area' ? '0 0 15%' : '1'};
    padding: 20px;
`;

//left filter bar
const Container = styled.div`
    width: 100%;
    padding: 10px;
    box-sizing: border-box;

    border-top: 1px solid #ccc;
`;

const Title = styled.div`
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 15px;
`;

const CheckboxList = styled.div`
    display: flex;
    flex-direction: column;
`;

const CheckboxItem = styled.div`
    margin-bottom: 10px;

    input[type="checkbox"] {
        margin-right: 5px;
    }
`;


export default Category;
