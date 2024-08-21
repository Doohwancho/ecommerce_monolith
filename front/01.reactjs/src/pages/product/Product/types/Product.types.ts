import { DiscountDTO } from '../../../../../models/src/model/discount-dto';

interface GroupedProductItemOption { 
  optionId: number;
  optionVariationId: number;
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
  // options: ProductOptions;
  [optionName: string]: GroupedProductItemOption | string | number | undefined
}

interface DiscountProps {
  discounts: DiscountDTO[];
}

interface ProductDetailProps {
  product: GroupedProductItems;
  chosenOption: GroupedProductItemOption | null;
  onOptionChange: (optionName: string) => void;
}

export type{ GroupedProductItemOption, GroupedProductItems, DiscountProps, DiscountDTO, ProductDetailProps};