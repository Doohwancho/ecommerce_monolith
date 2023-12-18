import React, { useState, memo } from 'react';
import { Link } from 'react-router-dom';
import { SiNike } from 'react-icons/si';
import { TopContainer, TopNavWrapper, NavLeft, NavCenter } from './styles/TopNav.styles';
import { getUniqueTopCategories, filterCategoriesForTopId } from './util/TopNav.utils';
import { AllCategoriesByDepthResponseDTO } from '../../../models/src/model/all-categories-by-depth-response-dto';

import { useFetchCategories } from './hooks/useFetchCategories';
import { useScrollHandler } from './hooks/useScrollHandler';

import Modal from './modal/Modal';


const TopNav: React.FC = () => {
  const { categories } = useFetchCategories();
  const [selectedCategories, setSelectedCategories] = useState<AllCategoriesByDepthResponseDTO[]>([]);
  const [modalOn, setModalOn] = useState(false);
  const hide = useScrollHandler();

  return (
    <>
      {modalOn ? <Modal setMenModalOn={setModalOn} categories={selectedCategories} /> : null}
      <TopContainer>
        <TopNavWrapper className={hide ? 'hide' : ''}>
          <NavLeft>
            <Link to="/">
              <SiNike />
            </Link>
          </NavLeft>
          <NavCenter>
            <div className="mainMenu">
              {getUniqueTopCategories(categories).map((category) => (
                <div 
                  key={category.topCategoryId}
                  onMouseEnter={() => {
                    setModalOn(true);
                    setSelectedCategories(filterCategoriesForTopId(categories, category.topCategoryId));
                }}>
                    {category.topCategoryName}
                </div>
              ))}
            </div>
          </NavCenter>
        </TopNavWrapper>
      </TopContainer>
    </>
  );
}

export default memo(TopNav);
