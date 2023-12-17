
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

interface InitialOptionFilterType { 
    [key: string]: string[] 
};

interface InitialPriceFiltersType { 
    min: number; max: number 
};
  
interface CategoryFiltersProps {
    optionsData: GroupedOptions[];
    priceFilters: InitialPriceFiltersType[];
    onOptionChange: (optionId: number, optionVariationName: string, isChecked: boolean, event: React.ChangeEvent<HTMLInputElement>) => void;
    onPriceChange: (range: InitialPriceFiltersType) => void;
  }
  
  interface CategoryProductsProps {
    productsData: GroupedProducts;
}

export type {GroupedOptions, GroupedProducts, GroupedProductInfo, OptionVariation, OptionFilters, InitialOptionFilterType, InitialPriceFiltersType, CategoryFiltersProps, CategoryProductsProps};