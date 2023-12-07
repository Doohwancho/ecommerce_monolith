import React, { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from 'react-query';
import { useRecoilValue } from 'recoil';
import { categoriesState } from '../store/state'; // Adjust the import path as needed
import { AllCategoriesByDepthResponseDTO, OptionsOptionVariationsResponseDTO } from 'model';

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

const Category = () => {
  const { categoryName } = useParams();
  const categories = useRecoilValue(categoriesState);
  const { data: optionsData, isLoading, error } = useQuery<OptionsOptionVariationsResponseDTO[], Error>(
    ['categoryOptions', 71],
    () => fetchCategoryOptions(71),
  );

  useEffect(() => {
    console.log("Updated categories:", categories);
    console.log("updated option datas:", optionsData);
  }, [categories, optionsData]); 


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
    </>
  );
};

export default Category;
