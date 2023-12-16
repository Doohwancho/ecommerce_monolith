import { useState, useEffect } from 'react';

export const useFilterProducts = (groupedProducts, initialOptionFilter, initialPriceFilters) => {
    const [priceFilters, setPriceFilters] = useState(initialPriceFilters);
    const [optionFilter, setOptionFilter] = useState(initialOptionFilter);
    const [filteredProducts, setFilteredProducts] = useState({});
  
  useEffect(() => {
    if (Object.keys(optionFilter).length > 0 || priceFilters.length > 0) {
      setFilteredProducts(filterGroupedProducts(groupedProducts));
    } else {
      setFilteredProducts(groupedProducts);
    }
  }, [groupedProducts, priceFilters, optionFilter]);

  const handleOptionFilterChange = (optionId: string, optionName: string, optionVariationName: string, isChecked: boolean, event) => {    
    event.stopPropagation();
    
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


  const resetFilters = () => {
    setOptionFilter(initialOptionFilter);
    setPriceFilters(initialPriceFilters);
  };

  const filterGroupedProducts = (groupProducts: GroupedProducts) => {

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


  return { filteredProducts, priceFilters, handleOptionFilterChange, handlePriceFilterChange, resetFilters };
};


