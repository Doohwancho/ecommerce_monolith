import { useState, useEffect } from 'react';
import { Wrapper } from './styles/Product.styles';
import { GroupedProductItemOption, GroupedProductItems } from './types/Product.types';
import { isValidProductId } from './util/Product.util';
import { useParams } from 'react-router-dom';
import { useProductData } from './hooks/useProductData';
import ProductImages from './component/productImages/ProductImages';
import ProductDetail from './component/productDetails/ProductDetail';


const Product = () => {
  const { productId } = useParams<{ productId: string }>();
  const { groupedProductItems, isLoading: productIsLoading, error: productError } = useProductData(productId);
  const [chosenOption, setChosenOption] = useState<GroupedProductItemOption | null>(null);

  const images = ["/images/category-product-image-1.webp", "/images/category-product-image-2.webp", "/images/category-product-image-3.webp", "/images/category-product-image-4.webp", "/images/category-product-image-5.webp"];

  //Q. why useEffect triggered 3 times?
  //1. initial render 때 component mount 될 때 실행됨
  //2. react-query가 fetch 실행할 때, productsData가 처음에 undefined or null이긴 하지만, productsData를 react-query가 건드리므로, 이 변수를 dependency로 받고있던 useEffect가 triggered 된다.
  //3. react-query가 끝나고, productsData가 undefined -> 값으로 찰 때, 3번째로 useEffect()가 실행된다.
  useEffect(() => {
    setChosenOption(findFirstProductOption(groupedProductItems));
  }, [groupedProductItems]); 

  const findFirstProductOption = (groupedItems?: GroupedProductItems) => {
    if (!groupedItems) {
      return null;
    }
    const firstOptionName = Object.keys(groupedItems).find(key => typeof groupedItems[key] === 'object');
    return firstOptionName ? groupedItems[firstOptionName] as GroupedProductItemOption : null;
  };
  
  const handleOptionChange = (optionName: string) => {
    if (groupedProductItems && optionName in groupedProductItems) {
      setChosenOption(groupedProductItems[optionName] as GroupedProductItemOption);
    }
  };
  
  if (productIsLoading) return <div>Loading...</div>;
  if (!isValidProductId(productId)) {
    return <div>Error: Invalid product ID</div>;
  }
  if (productError) {
    const errorMessage = (productError as any).message || 'Unknown error';
    return <div>Error: {errorMessage}</div>;
  }

  return (
    <>
      <Wrapper>
        <div className='section section-center page'>
          <div className='product-center'>
            <ProductImages images={images} />
            {groupedProductItems && (
              <ProductDetail product={groupedProductItems} chosenOption={chosenOption} onOptionChange={handleOptionChange} />
            )}
          </div>
          <div className='empty-line'></div>
        </div>
      </Wrapper>
    </>
  );
};


export default Product;
