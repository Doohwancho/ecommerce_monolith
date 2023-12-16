import { useQuery } from 'react-query';
import { useMemo } from 'react';
import { fetchCategoryOptions, fetchProductsWithOptionsByCategoryId } from '../service/CategoryService';

import { OptionsOptionVariationsResponseDTO, ProductWithOptionsListResponseDTO, ProductWithOptionsDTO } from 'model';
import {GroupedOptions, GroupedProducts, GroupedProductInfo, OptionVariation, OptionFilters} from '../types/Category.types';

const groupOptionsByOptionId = (data: OptionsOptionVariationsResponseDTO[]): GroupedOptions[] => { //TODO - useMemo() to prevent unnecessary re-render
    const grouped: Record<number, GroupedOptions> = {};

    data?.forEach(item => {
        const { categoryId, optionId, optionName, optionVariationName } = item;
        if (!grouped[optionId]) {
            grouped[optionId] = { categoryId, optionId, optionName, optionVariationNames: [] };
        }
        grouped[optionId].optionVariationNames.push(optionVariationName);
    });

    return Object.values(grouped);
};

const groupProducts = (products: ProductWithOptionsDTO[]): GroupedProducts => { //TODO - useMemo() to prevent unnecessary re-render
  const groupedProducts: GroupedProducts = {};

  products?.forEach(product => {
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


export const useCategoryData = (categoryId: number) => {
    // Fetching category options
    const { data: optionsData, status: optionsStatus, ...optionsQuery } = useQuery(
        ['categoryOptions', categoryId], 
        () => fetchCategoryOptions(categoryId), 
        { enabled: !!categoryId }
    );

    // Fetching products with options by category ID
    const { data: productsData, status: productsStatus, ...productsQuery } = useQuery(
        ['productsWithOptions', categoryId], 
        () => fetchProductsWithOptionsByCategoryId(categoryId),
        { enabled: !!categoryId }
    );

    // Grouped options and products, using useMemo for optimization
    const groupedOptions = useMemo(() => {
        if (optionsStatus === 'success' && optionsData) {
            return groupOptionsByOptionId(optionsData);
        }
        return [];
    }, [optionsData, optionsStatus]); //TODO - is it corect to use 'useMemo()' here?
    
    const groupedProducts = useMemo(() => {
        if (productsStatus === 'success' && productsData?.products) {
            return groupProducts(productsData.products);
        }
        return {}; // or appropriate default value
    }, [productsData, productsStatus]);


    return { groupedOptions, groupedProducts, optionsQuery, productsQuery };
};
