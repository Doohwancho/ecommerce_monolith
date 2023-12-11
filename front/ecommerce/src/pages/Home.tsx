import React, { useEffect } from 'react';
import { useQuery } from 'react-query';
import { ProductDTO, AllCategoriesByDepthResponseDTO } from 'model';
import { useRecoilState } from 'recoil';
import { categoriesState } from '../store/state';
import { useNavigate } from 'react-router-dom';
import Header from '../components/common/Header'
import TopNav from '../components/common/TopNav/TopNav';
import Footer from '../components/common/Footer'

const fetchCategories = async (): Promise<AllCategoriesByDepthResponseDTO> => {
    const response = await fetch('http://127.0.0.1:8080/products/categories', { credentials: 'include' });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  };

const fetchTopTenRatedProducts = async (): Promise<ProductDTO[]> => {
  const response = await fetch('http://127.0.0.1:8080/products/highestRatings', { credentials: 'include' }); //credentials: 'include'를 안하면, 매 GET, POST request마다 다른 JSESSIONID를 내려받아 authorization이 꼬인다. 

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
  const { data: productsResponse, error, isLoading } = useQuery<ProductDTO[], Error>('products', fetchTopTenRatedProducts);

  useEffect(() => {
    console.log("Updated categories:", categories);
  }, [categories]); 

  return (
    <>
      <Header />
      <TopNav />
      <h1>Home Page!</h1>
      {isLoadingCategories && <p>Category Loading...</p>}
      {categoriesError && <p>Error: {categoriesError.message}</p>}

      <h2>Products</h2>
      {isLoading && <p>Loading...</p>}
      {error && <p>Error: {error.message}</p>}
      <ul>
        {productsResponse?.map((product: ProductDTO, index: number) => (
          <li key={index}>
            {product.productId}, {product.name} - {product.description}, {product.rating}, {product.ratingCount}, {product.categoryId}
          </li>
        ))}
      </ul>
      <Footer />
    </>
  );
};

export default Home;
