import React, { useEffect, useRef, useState } from 'react';
import { useRecoilState } from 'recoil';
import { categoriesState } from '../../../store/state';
import { AllCategoriesByDepthResponseDTO } from 'model';
import styled from 'styled-components';
import { SiNike } from 'react-icons/si';
import { Link, useNavigate } from 'react-router-dom';
import Modal from './modal/Modal';

const throttle = (callback: (...args: any[]) => void, waitTime: number) => {
  let timeId: ReturnType<typeof setTimeout> | null = null;
  return (element: any) => {
    if (timeId) {
      return (timeId = setTimeout(() => {
        callback.call(this, element);
        timeId = null;
      }, waitTime));
    }
  };
};

const getUniqueTopCategories = (categories: AllCategoriesByDepthResponseDTO[]) => {
  const uniqueCategories = new Map();

  categories?.forEach(category => {
    if (!uniqueCategories.has(category.topCategoryId)) {
      uniqueCategories.set(category.topCategoryId, category);
    }
  });

  return Array.from(uniqueCategories.values());
}

const filterCategoriesForTopId = (categories: AllCategoriesByDepthResponseDTO[], topCategoryId: number) => {
  return categories.filter(category => category.topCategoryId === topCategoryId);
};


const TopNav: React.FC = () => {
  const [categories] = useRecoilState<AllCategoriesByDepthResponseDTO[]>(categoriesState); // Fetch categories from recoil state
  const [modalOn, setModalOn] = useState(false);
  const [selectedCategories, setSelectedCategories] = useState([]);

  const [hide, setHide] = useState(false);
  const [pageY, setPageY] = useState(0);
  const documentRef = useRef<Document>(document);

  const handleScroll = () => {
    const { pageYOffset } = window;
    const deltaY = pageYOffset - pageY;
    const hide = pageYOffset !== 0 && deltaY >= 0;
    setHide(hide);
    setPageY(pageYOffset);
  };

  const throttleScroll = throttle(handleScroll, 50);

  useEffect(() => {
    documentRef.current.addEventListener('scroll', throttleScroll);
    return () => documentRef.current.removeEventListener('scroll', throttleScroll);
  }, [pageY]);

  return (
    <>
      {modalOn ? <Modal setMenModalOn={setModalOn} categories={selectedCategories} /> : null}
      <TopContainer>
        <TopNavWrapper className={hide && 'hide'}>
          <NavLeft>
            <Link to="/">
              <SiNike />
            </Link>
          </NavLeft>
          <NavCenter>
            <div className="mainMenu">
              {getUniqueTopCategories(categories).map((category, index) => (
                <a href={`/list?genderId=${category.topCategoryId}`} key={index}>
                  <div onMouseEnter={() => {
                    setModalOn(true);
                    setSelectedCategories(filterCategoriesForTopId(categories, category.topCategoryId));
                  }}>
                    {category.topCategoryName}
                  </div>
                </a>
              ))}
            </div>
          </NavCenter>
        </TopNavWrapper>
      </TopContainer>
    </>
  );
}

const TopContainer = styled.div`
  position: relative;
  width: 100%;
`;

const TopNavWrapper = styled.div`
  box-sizing: border-box;
  display: flex;
  justify-content: flex-start; 
  align-items: center;
  padding: 0 3vw;
  position: relative;
  left: 0;
  height: 80px;
  width: 100%;
  font-family: ${props => props.theme.fontContent};
  z-index: 100;
  background-color: white;

  @media screen and (max-width: 640px) {
    top: 0;
  }
`;

const NavLeft = styled.div`
  position: absolute;
  font-size: 3.5rem;

  a {
    color: black;

    & :hover {
      color: gray;
    }
  }
`;

const NavCenter = styled.div`
  justify-content: center;
  margin: 0 auto; 
  box-sizing: border-box;
  font-family: ${props => props.theme.fontContent};

  a {
    color: black;
    text-decoration: none;
  }

  .mainMenu {
    display: flex;

    & div {
      margin: 0 20px;
      cursor: pointer;
    }
  }

  @media screen and (max-width: 640px) {
    display: none;
  }
`;
export default TopNav;
