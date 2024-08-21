import { useState, useEffect } from 'react';
import { useQuery } from 'react-query';
import { isValidProductId } from '../util/Product.util';
import { fetchProductsByProductId } from '../service/ProductService';
import { GroupedProductItems } from '../types/Product.types';
import { ProductDetailResponseDTO } from '../../../../../models/src/model/product-detail-response-dto';


export const useProductData = (productId: string | undefined) => {
  const { data, isLoading, error } = useQuery(['productId', productId], () => fetchProductsByProductId(parseInt(productId!, 10)), {
    enabled: isValidProductId(productId),
  });
  const [groupedProductItems, setGroupedProductItems] = useState<GroupedProductItems>();

  useEffect(() => {
    if (data) {
      setGroupedProductItems(groupProductItems(data));
    }
  }, [data]);

  return { groupedProductItems, isLoading, error };
};


const groupProductItems = (productItems: ProductDetailResponseDTO[]): GroupedProductItems => {
  const groupedProducts: GroupedProductItems = {
    productId: productItems[0].productId,
    name: productItems[0].name,
    description: productItems[0].description,
    rating: productItems[0].rating,
    ratingCount: productItems[0].ratingCount,
    categoryId: productItems[0].categoryId,
    categoryName: productItems[0].categoryName,
    categoryCode: productItems[0].categoryCode,
  };

  productItems.forEach(productItem=> {
    const optionName = `${productItem.optionName}`;

    if (!groupedProducts[optionName]) {
        groupedProducts[optionName] = {
          optionId: productItem.optionId,
          optionVariationId: productItem.optionVariationId,
          optionVariationName: productItem.optionVariationName,
          quantity: productItem.quantity,
          price: productItem.price,
          discounts: productItem.discounts
        };
    }
  });

  return groupedProducts;
}