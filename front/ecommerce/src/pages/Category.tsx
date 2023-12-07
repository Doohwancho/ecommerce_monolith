import React, { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from 'react-query';
import { useRecoilValue } from 'recoil';
import { categoriesState } from '../store/state'; // Adjust the import path as needed
import { AllCategoriesByDepthResponseDTO, OptionsOptionVariationsResponseDTO, ProductListResponseDTO, ProductDTO } from 'model';
import { useNavigate } from 'react-router-dom';

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

  const fetchProductsByCategoryId = async (categoryId: number): Promise<ProductListResponseDTO> => {
    const baseUrl = 'http://127.0.0.1:8080';
    const endpoint = `/products/category/${categoryId}`;
    const fullUrl = baseUrl + endpoint;

    const response = await fetch(fullUrl, { credentials: 'include' });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  };  

const Category = () => {
  const navigate = useNavigate();
  const { categoryName } = useParams();
  const categories = useRecoilValue(categoriesState);
  const { data: optionsData, optionIsLoading, optionError } = useQuery<AllCategoriesByDepthResponseDTO[], Error>(
    ['categoryOptions', 71], //?
    () => fetchCategoryOptions(71),
  );

  const { data: productsData, productIsLoading, productError } = useQuery<ProductListResponseDTO, Error>(
    ['categoryId', 71],
    () => fetchProductsByCategoryId(71),
  );

  useEffect(() => {
    console.log("Updated categories:", categories);
    console.log("updated option datas:", optionsData);
    console.log("updated products by categoryId", productsData);
  }, [categories, optionsData, productsData]); 

  const onProductClick = (productId: number) => {
    navigate(`/product/${productId}`);
  };

  return (
    <>
        <div>
            <h1>Top Categories</h1>
            <h1>Category: {categoryName}</h1>
            {categories && categories.length > 0 ? (
            <ul>
                {categories.map((category: AllCategoriesByDepthResponseDTO, index: number) => (
                <li key={index}>
                    Top: {category.topCategoryName} (ID: {category.topCategoryId}),
                    Mid: {category.midCategoryName} (ID: {category.midCategoryId}),
                    Low: {category.lowCategoryName} (ID: {category.lowCategoryId})
                </li>
                ))}
            </ul>
            ) : (
            <p>No categories available.</p>
            )}
        </div> 

        <div>
            <h1>Options for Category: {categoryName}</h1>
            {optionsData && optionsData.length > 0 ? (
                <ul>
                {optionsData.map((option, index) => (
                    <li key={index}>
                    Option: {option.optionName}, Variations: {option.optionVariationName}
                    </li>
                ))}
                </ul>
            ) : (
                <p>No options available for this category.</p>
            )}
        </div>

        <div>
            <h1>Product List that belongs to categoryId</h1>
            {productsData && productsData.products.length > 0 ? (
                <ul>
                {productsData.products.map((product: ProductDTO, index: number) => (
                    <li key={index} onClick={() => onProductClick(product.productId)}>
                        product: {product.name}, description: {product.description}
                    </li>
                ))}
                </ul>
            ) : (
                <p>No products available for this category.</p>
            )}
        </div>
    </>
  );
};

export default Category;
