import { useState, useEffect } from 'react';
import { useQuery } from 'react-query';
import { fetchProductsByProductId } from '../service/ProductService';
import { GroupedProductItems } from '../types/Product.types';
import { ProductDetailResponseDTO } from 'model';


export const useProductData = (productId: number) => {
  const { data, isLoading, error } = useQuery(['productId', productId], () => fetchProductsByProductId(productId));
  const [groupedProductItems, setGroupedProductItems] = useState({});

  useEffect(() => {
    if (data) {
      setGroupedProductItems(groupProductItems(data));
    }
  }, [data]);

  return { groupedProductItems, isLoading, error };
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