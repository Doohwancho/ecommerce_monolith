import { DiscountDTO } from '../../../../../models/src/model/discount-dto';

interface GroupedProductItemOption { //TODO - 장바구니, 결제페이지에 넘기기 위한 optionVariation id 추가 필요
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
  [optionName: string]: GroupedProductItemOption | string | number | undefined //TODO - 장바구니, 결제페이지에 넘기기 위한 option id 추가 필요
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