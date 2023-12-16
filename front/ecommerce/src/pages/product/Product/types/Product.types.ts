import { ProductDetailResponseDTO, DiscountDTO } from 'model';

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

export { GroupedProductItemOption, GroupedProductItems };