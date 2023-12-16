
interface GroupedOptions {
    categoryId: number;
    optionId: number;
    optionName: string;
    optionVariationNames: string[];
  }
  
interface GroupedProducts {
    [productId: string]: GroupedProductInfo;
}

interface GroupedProductInfo {
    productId: number;
    name: string;
    description: string;
    optionVariations: { [optionId: number]: OptionVariation };
    categoryId: number;
    categoryName: string;
    averagePrice: number;
    totalQuantity: number;
    count: number;
}

interface OptionVariation {
    [optionName: string]: string;
}

interface OptionFilters {
    [key: string]: string;
}

export {GroupedOptions, GroupedProducts, GroupedProductInfo, OptionVariation, OptionFilters};