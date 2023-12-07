import React, { useEffect } from 'react';
import { useQuery } from 'react-query';
import { PaginatedProductResponse, ProductDTO, AllCategoriesByDepthResponseDTO } from 'model';
import { useRecoilState } from 'recoil';
import { categoriesState } from '../store/state';
import { useNavigate } from 'react-router-dom';

const fetchCategories = async (): Promise<AllCategoriesByDepthResponseDTO> => {
    const response = await fetch('http://127.0.0.1:8080/products/categories', { credentials: 'include' });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  };

const fetchProducts = async (): Promise<PaginatedProductResponse> => {
  const response = await fetch('http://127.0.0.1:8080/products/', { credentials: 'include' }); //credentials: 'include'를 안하면, 매 GET, POST request마다 다른 JSESSIONID를 내려받아 authorization이 꼬인다. 

  if (!response.ok) {
    console.log("response is not okay!")
    throw new Error('Network response was not ok');
  }

  return response.json();
};

const Home: React.FC = () => {
  const [categories, setCategories] = useRecoilState(categoriesState);
  const navigate = useNavigate();

  const { data: categoriesResponse, isLoading: isLoadingCategories, error: categoriesError } = useQuery<AllCategoriesByDepthResponseDTO, Error>('categories', fetchCategories, {
    onSuccess: (data) => {
      setCategories(data);
    }
  });
  const { data: productsResponse, error, isLoading } = useQuery<PaginatedProductResponse, Error>('products', fetchProducts);

  useEffect(() => {
    console.log("Updated categories:", categories);
  }, [categories]); 

  const onCategoryClick = (categoryName: string) => {
    navigate(`/category/${categoryName}`);
  };

  return (
    <>
      <h1>Home Page!</h1>
      <h2>Categories</h2>
      {isLoadingCategories && <p>Category Loading...</p>}
      {categoriesError && <p>Error: {categoriesError.message}</p>}
      <ul>
        {categoriesResponse?.map((category: AllCategoriesByDepthResponseDTO, index: number) => (
            <li key={index} onClick={() => onCategoryClick(category.lowCategoryName)}>
                Top: {category.topCategoryName} (ID: {category.topCategoryId}),
                Mid: {category.midCategoryName} (ID: {category.midCategoryId}),
                Low: {category.lowCategoryName} (ID: {category.lowCategoryId})
          </li>
        ))}
      </ul>
      <h2>Products</h2>
      {isLoading && <p>Loading...</p>}
      {error && <p>Error: {error.message}</p>}
      <ul>
        {productsResponse?.content.map((product: ProductDTO, index: number) => (
          <li key={index}>
            {product.productId}, {product.name} - {product.description}, {product.rating}, {product.ratingCount}, {product.categoryId}
          </li>
        ))}
      </ul>
    </>
  );
};

export default Home;
