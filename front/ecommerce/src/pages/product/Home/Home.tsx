import React from 'react';
import {MainWrapper, MainElement, MainContent} from './styles/Home.styles'

const Home: React.FC = () => {
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
    </>
  );
};

export default Home;
