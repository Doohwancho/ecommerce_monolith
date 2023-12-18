import { useEffect, useRef } from 'react';
import { useParams } from 'react-router-dom';

import {MainContainer, Area } from './styles/Category.styles';
import { useCategoryData } from './hooks/useCategoryData';
import { useFilterProducts } from './hooks/useFilterProducts';
import CategoryFilters from './ui/CategoryFilters';
import CategoryProducts from './ui/CategoryProducts';


const Category = () => {
  const { lowCategoryId = '' } = useParams();
  const prevCategoryIdRef = useRef<string | undefined >();

  const {
      groupedOptions,
      groupedProducts,
      optionsQuery: { refetch: refetchOptions },
      productsQuery: { refetch: refetchProducts }
  } = useCategoryData(lowCategoryId);

  const {
    filteredProducts,
    priceFilters,
    handleOptionFilterChange,
    handlePriceFilterChange,
    resetFilters
  } = useFilterProducts(groupedProducts, {}, []);


  useEffect(() => {
    //if category page is chagned, refetch option, products, and empty previous option filters
    if (lowCategoryId && lowCategoryId !== prevCategoryIdRef.current) {
      refetchOptions();
      refetchProducts();
      resetFilters();
      prevCategoryIdRef.current = lowCategoryId; // Update the ref with the new categoryId
    }
  }, [lowCategoryId]); 

  return (
    <MainContainer>
      <Area className="left-area">
        <CategoryFilters optionsData={groupedOptions} priceFilters={priceFilters} onOptionChange={handleOptionFilterChange} onPriceChange={handlePriceFilterChange} />
      </Area>
      <Area className="right-area">
        <CategoryProducts productsData={filteredProducts} />
      </Area>
    </MainContainer>
  );
};

export default Category;
