import React from 'react';
import { useQuery } from 'react-query';
import { PaginatedProductResponse, ProductDTO } from 'model';

const fetchProducts = async (): Promise<PaginatedProductResponse> => {
  const response = await fetch('http://127.0.0.1:8080/products/', { credentials: 'include' }); //credentials: 'include'를 안하면, 매 GET, POST request마다 다른 JSESSIONID를 내려받아 authorization이 꼬인다. 

  if (!response.ok) {
    console.log("response is not okay!")
    throw new Error('Network response was not ok');
  }

  return response.json();
};

const Home: React.FC = () => {
  const { data: productsResponse, error, isLoading } = useQuery<PaginatedProductResponse, Error>('products', fetchProducts);

  return (
    <>
      <h1>Home Page!</h1>
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
