import React, { useEffect } from 'react';
import styled from 'styled-components';


// const fetchTopTenRatedProducts = async (): Promise<ProductDTO[]> => {
//   const response = await fetch('http://127.0.0.1:8080/products/highestRatings', { credentials: 'include' }); //credentials: 'include'를 안하면, 매 GET, POST request마다 다른 JSESSIONID를 내려받아 authorization이 꼬인다. 

//   if (!response.ok) {
//     console.log("response is not okay!")
//     throw new Error('Network response was not ok');
//   }

//   return response.json();
// };

const Home: React.FC = () => {
  // const { data: productsResponse, error, isLoading } = useQuery<ProductDTO[], Error>('products', fetchTopTenRatedProducts);

  console.log("-----------------------------");
  console.log("Home.tsx rendered!");

  return (
    <>
      <MainWrapper>
        <MainElement>
          <img className="main-image" src="/images/main-image.webp" alt="main image" />
          <MainContent>
            <div className="title">가능성은 지금부터</div>
            <div className="content">매일 공개되는 새로운 미션에 도전하고</div>
            <div className="content">
              더 성장한 나를 만들어 줄 다양한 리워드와 제품도 함께 만나보세요
            </div>
            <div>
              <button>미션 참여하기</button>
              <button>컬랙션 구매하기</button>
            </div>
          </MainContent>
        </MainElement>
      </MainWrapper>

      {/* <ul>
        {productsResponse?.map((product: ProductDTO, index: number) => (
          <li key={index}>
            {product.productId}, {product.name} - {product.description}, {product.rating}, {product.ratingCount}, {product.categoryId}
          </li>
        ))}
      </ul> */}
    </>
  );
};

const MainWrapper = styled.div`
  margin: 3vw;
  max-width: 100vw;
  font-family: ${({ theme }) => theme.fontContent};

  .pc {
    display: block;
  }

  .mobile {
    display: none;
  }

  .title {
    margin: 30px;
    font-size: 50px;
    font-weight: 900;
  }

  .content {
    margin: 5px;
  }

  button {
    margin: 30px;
    background-color: black;
    color: white;
    padding: 14px 36px;
    border: none;
    border-radius: 20px;
    font-size: 14px;
  }

  @media screen and (max-width: 640px) {
    .pc {
      display: none;
    }
    .mobile {
      display: block;
    }
  }
`;

const MainElement = styled.div`
  width: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;

  img {
    max-width: 100%;
    height: auto;
    display: block;
  }
`;

const MainContent = styled.div`
  margin-bottom: 60px;
  text-align: center;
`;

export default Home;
