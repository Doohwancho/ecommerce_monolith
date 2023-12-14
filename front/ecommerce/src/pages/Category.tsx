import React, { useState, useEffect, useRef } from 'react';
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

interface GroupedProducts {
  [productId: string]: GroupedProductInfo;
}
interface GroupedProductInfo {
  productId: number;
  name: string;
  description: string;
  optionVariations: { [optionId: number]: OptionVariation };
  categoryId: number;
  categoryName: string;
  averagePrice: number;
  totalQuantity: number;
  count: number;
}

interface OptionVariation {
  [optionName: string]: string;
}

interface OptionFilters {
  [key: string]: string;
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

const groupProducts = (products: ProductWithOptionsDTO[]): GroupedProducts => {
  const groupedProducts: GroupedProducts = {};

  products.forEach(product => {
    const key = `${product.productId}`;
    if (!groupedProducts[key]) {
        groupedProducts[key] = {
            productId: product.productId,
            name: product.name,
            description: product.description,
            categoryId: product.categoryId,
            categoryName: product.categoryName,
            optionVariations: {},
            averagePrice: 0,
            totalQuantity: 0,
            count: 0
        };
    }
    if (!groupedProducts[key].optionVariations[product.optionId]) {
      groupedProducts[key].optionVariations[product.optionId] = {};
    }
    groupedProducts[key].optionVariations[product.optionId][product.optionName] = product.optionVariationName;
    groupedProducts[key].averagePrice += product.price;
    groupedProducts[key].totalQuantity += product.quantity;
    groupedProducts[key].count += 1;
  });

    // Calculate the average price for each product
  for (const key in groupedProducts) {
      groupedProducts[key].averagePrice =  Math.round(groupedProducts[key].averagePrice / groupedProducts[key].count);
  }

  return groupedProducts;
}


const Category = () => {
  const navigate = useNavigate();
  const { lowCategoryId } = useParams();
  const prevCategoryIdRef = useRef();

  const [priceFilters, setPriceFilters] = useState([]); // State for price filter
  const [optionFilter, setOptionFilter] = useState({}); // State for option variation filter
  const [filteredProducts, setFilteredProducts] = useState({}); // New state for filtered products

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
    //if category page is chagned, refetch option, products, and empty previous option filters
    if (lowCategoryId && lowCategoryId !== prevCategoryIdRef.current) {
      console.log("low categoryId is changed, so refetch options and products!");
      refetchOptions();
      refetchProducts();
      setOptionFilter({}); //reset option filters
      setPriceFilters([]); //reset price filters
      prevCategoryIdRef.current = lowCategoryId; // Update the ref with the new categoryId
    }
    
    //after fetching products, group them by productId, then filter them if option | price filters exist
    if(productsData) {
      console.log("step1: ",productsData.products);

      const groupedProducts = groupProducts(productsData.products);
      console.log("step2: ", groupedProducts);

      console.log("price filters: ", priceFilters);
      if(Object.keys(optionFilter).length > 0 || priceFilters.length > 0) {
        const updatedFilteredProducts = filteredGroupedProducts(groupedProducts);
        console.log("step3: ", updatedFilteredProducts);
        setFilteredProducts(updatedFilteredProducts);
      } else {
        console.log("optionFilter is empty! so dont apply filter!");
        setFilteredProducts(groupedProducts);
      }
      
    }
  }, [lowCategoryId, optionsData, productsData, priceFilters, optionFilter]); 

  const onProductClick = (productId: number) => {
    navigate(`/product/${productId}`);
  };

  const handleOptionFilterChange = (optionId: string, optionName: string, optionVariationName: string, isChecked: boolean, event) => {    
    event.stopPropagation();
    console.log(event.target);
    console.log("option clicked! optionId: "+optionId + " optionName: "+optionName + " optionVariationName: "+optionVariationName + " isChecked: "+isChecked);
    setOptionFilter(prevFilters => {
      const newFilters = { ...prevFilters };

      if (isChecked) {  
        // If the filter for this optionId doesn't exist, initialize it
        if (!newFilters[optionId]) {
          newFilters[optionId] = [];
        }
        // Add the variation name to the filter
        if (!newFilters[optionId].includes(optionVariationName)) {
          newFilters[optionId].push(optionVariationName);
        }
      } else {
        // Remove the variation name from the filter
        newFilters[optionId] = newFilters[optionId].filter(variation => variation !== optionVariationName);

        // If the filter array is empty, delete the filter
        if (newFilters[optionId].length === 0) {
          delete newFilters[optionId];
        }
      }
      console.log("inside handleOptionFilterChange()", newFilters);
      Object.keys(newFilters).map((key) => {
        console.log("key: "+key+" newfilter[key]: "+newFilters[key])
      });

      return newFilters;
    });
  };

  const handlePriceFilterChange = (range) => {
    setPriceFilters(prevFilters => {
      if (prevFilters.some(filter => filter.min === range.min && filter.max === range.max)) {
        // Remove the filter
        return prevFilters.filter(filter => filter.min !== range.min || filter.max !== range.max);
      } else {
        // Add the filter
        return [...prevFilters, range];
      }
    });
  };

  const filteredGroupedProducts = (groupProducts: GroupedProducts) => {
    console.log("inside filteredGroupProducts(), filter:", optionFilter);
    console.log("inside filteredGroupProducts()", groupProducts);

    return Object.values(groupProducts).filter(product => {
      // Check if product passes any of the selected price filters
      const priceFilterPassed = priceFilters.length === 0 || priceFilters.some(filter => 
        product.averagePrice >= filter.min && product.averagePrice <= filter.max
      );
  
      // Check if product passes the option variation filter
      const optionFilterPassed = Object.keys(optionFilter).length === 0 || Object.entries(optionFilter).some(([optionId, optionVariationNames]) =>
        product.optionVariations[optionId] && optionVariationNames.includes(Object.values(product.optionVariations[optionId])[0])
      );
  
      return priceFilterPassed && optionFilterPassed;
    });
  };

  console.log("-----------------------------");
  console.log("Category.tsx rendered!");

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
                      <input 
                        type="checkbox" 
                        onChange={(e) => handleOptionFilterChange(group.optionId, group.optionName, variation, e.target.checked, e)}
                      />
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
                  <input 
                    type="checkbox" 
                    checked={!!priceFilters.some(filter => filter.min === 0 && filter.max === 50000)}
                    onChange={() => handlePriceFilterChange({ min: 0, max: 50000 })}
                  />
                  <label>
                      0 - 50,000원
                  </label>
              </CheckboxItem>
            </CheckboxList>
            <CheckboxList>
              <CheckboxItem>
                  <input 
                    type="checkbox" 
                    checked={!!priceFilters.some(filter => filter.min === 50000 && filter.max === 100000)}
                    onChange={() => handlePriceFilterChange({ min: 50000, max: 100000 })}
                  />
                  <label>
                      50,000 - 100,000원
                  </label>
              </CheckboxItem>
            </CheckboxList>
            <CheckboxList>
              <CheckboxItem>
                  <input 
                    type="checkbox" 
                    checked={!!priceFilters.some(filter => filter.min === 100000 && filter.max === 150000)}
                    onChange={() => handlePriceFilterChange({ min: 100000, max: 150000 })}
                  />
                  <label>
                    100,000 - 150,000원
                  </label>
              </CheckboxItem>
            </CheckboxList>
            <CheckboxList>
              <CheckboxItem>
                  <input 
                    type="checkbox" 
                    checked={!!priceFilters.some(filter => filter.min === 150000 && filter.max === 200000)}
                    onChange={() => handlePriceFilterChange({ min: 150000, max: 200000 })}
                  />
                  <label>
                    150,000 - 200,000원
                  </label>
              </CheckboxItem>
            </CheckboxList>
            <CheckboxList>
              <CheckboxItem>
                  <input 
                    type="checkbox" 
                    checked={!!priceFilters.some(filter => filter.min === 200000 && filter.max === Infinity)}
                    onChange={() => handlePriceFilterChange({ min: 200000, max: Infinity })}
                  />
                  <label>
                      200,000원 +
                  </label>
              </CheckboxItem>
            </CheckboxList>
          </Container>
        </Area>
        <Area className="right-area">
          <ProductContainer>
            {productsData && productsData.products.length > 0 ? (
                Object.values(filteredProducts).map((product) => (
                  <Card key={product.productId} onClick={() => onProductClick(product.productId)}>
                    {/* source: https://codepen.io/mdshifut/pen/VrwBJq */}
                    {/* <Badge>Hot</Badge> */}
                    {/* <ProductThumb> */}
                      <ProductImage src="/images/category-product-image-1.png" alt="" />
                    {/* </ProductThumb> */}
                    <ProductDetails>
                        <ProductCategory>{product.categoryName}</ProductCategory>
                        <ProductTitle><a href="">{product.name}</a></ProductTitle>
                        <ProductDescription>{product.description}</ProductDescription>
                        <ProductBottomDetails>
                            <ProductPrice>
                              {/* <ProductPriceSmall>$96.00</ProductPriceSmall> */}
                              {product.averagePrice} 원
                            </ProductPrice>
                        </ProductBottomDetails>
                    </ProductDetails>
                  </Card>
              ))
              ) : (
                  <p>No products available for this category.</p>
              )}
          </ProductContainer>
        </Area>
      </MainContainer>
      <Footer />
    </>
  );
};

const MainContainer = styled.div`
    display: flex;
    // width: 100%;
    width: 100vw;
`;

const Area = styled.div`
    flex: ${props => props.className === 'left-area' ? '0 0 15vw' : 'flex'};
    flex-wrap: wrap;
    justify-content: space-around;
    // padding: 20px;
`;

//left area for filter bar
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

//right area for product cards
const ProductContainer = styled.div`
  display: flex;
  flex-wrap: wrap;
  // width: 100%;
  width: 82vw;
  // justify-content: space-around;
  justify-content: flex-start; // Adjusted for alignment
  padding: 10px; // Optional, for some spacing around the container
`;

const Card = styled.div`
  // flex: 0 0 calc(33.333% - 20px); // Adjust width to fit three items per row
  // position: relative;
  width: calc(33.333% - 20px); 
  box-shadow: 0 2px 7px #dfdfdf;
  margin: 10px; // Adjust margin for spacing
  background: #fafafa;
    // width: 380px;
    // position: relative;
    // box-shadow: 0 2px 7px #dfdfdf;
    // margin: 50px auto;
    // background: #fafafa;
`;

const Badge = styled.div`
    position: absolute;
    left: 0;
    top: 20px;
    text-transform: uppercase;
    font-size: 13px;
    font-weight: 700;
    background: red;
    color: #fff;
    padding: 3px 10px;
`;

const ProductThumb = styled.div`
    display: flex;
    align-items: center;
    justify-content: center;
    height: 300px;
    padding: 50px;
    background: #f0f0f0;
`;

const ProductImage = styled.img`
    max-width: 100%;
    max-height: 100%;
`;

const ProductDetails = styled.div`
    padding: 30px;
`;

const ProductCategory = styled.span`
    display: block;
    font-size: 12px;
    font-weight: 700;
    text-transform: uppercase;
    color: #ccc;
    margin-bottom: 18px;
`;

const ProductTitle = styled.h4`
    font-weight: 500;
    text-transform: uppercase;
    color: #363636;
    margin-bottom: 18px;
    &:hover {
        color: #fbb72c;
    }
`;

const ProductDescription = styled.p`
    font-size: 15px;
    line-height: 22px;
    margin-bottom: 18px;
    color: #999;
`;

const ProductBottomDetails = styled.div`
    overflow: hidden;
    border-top: 1px solid #eee;
    padding-top: 20px;
`;

const ProductPrice = styled.div`
    float: left;
    width: 50%;
    font-size: 18px;
    color: #fbb72c;
    font-weight: 600;
`;

const ProductPriceSmall = styled.small`
    font-size: 80%;
    font-weight: 400;
    text-decoration: line-through;
    display: inline-block;
    margin-right: 5px;
`;

const ProductLinks = styled.div`
    text-align: right;
    float: left;
    width: 50%;
`;

const ProductLink = styled.a`
    display: inline-block;
    margin-left: 5px;
    color: #e1e1e1;
    transition: 0.3s;
    font-size: 17px;
    &:hover {
        color: #fbb72c;
    }
`;


export default Category;
