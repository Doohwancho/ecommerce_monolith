import React from 'react';
import Link from 'next/link';
import { AllCategoriesByDepthResponseDTO } from '../../../models';

  interface ModalProps {
    setMenModalOn: (value: boolean) => void;
    categories: AllCategoriesByDepthResponseDTO[];
  }
  
  interface LowCategory {
    id: number | string;
    name: string;
  }
  
  interface CategoryGroupProps {
    midCategoryName: string;
    lowCategories: LowCategory[];
  }

  //이 유틸함수 역시 모달에서만 쓰는 함수니까 따로 분리하지 말자. 
  const groupLowCategories = (categories: AllCategoriesByDepthResponseDTO[]): CategoryGroupProps[] => {
    const grouped: { [key: string]: CategoryGroupProps } = {};
  
    categories.forEach(category => {
      const midId = category.midCategoryId;
      if (midId !== undefined) { // Check if midId is not undefined
        if (!grouped[midId]) {
          grouped[midId] = {
            midCategoryName: category.midCategoryName ?? 'Unknown', // Provide a default value for midCategoryName
            lowCategories: []
          };
        }
  
        if (category.lowCategoryId !== undefined) { // Check if lowCategoryId is not undefined
          grouped[midId].lowCategories.push({
            id: category.lowCategoryId, // Already checked for undefined
            name: category.lowCategoryName ?? 'Unknown' // Provide a default value for lowCategoryName
          });
        }
      }
    });
  
    return Object.values(grouped);
  }
  
//CategoryGroup은 모달 안에서만 쓰는 컴포넌트니까 따로 모듈화 하지 말자. 만약 다른 컴포넌트에서도 CategoryGroup을 쓰면 그 때 분리하자. 
const CategoryGroup: React.FC<{ group: CategoryGroupProps }> = ({ group }) => (
  <ul className="p-10">
    <li className="mb-4 font-bold text-lg text-black">{group.midCategoryName}</li>
    <ul className="mt-0">
      {group.lowCategories.map((lowCategory: LowCategory, subIndex: number) => (
        <li key={subIndex} className="mb-2 text-sm text-gray-600 hover:text-gray-900">
          <Link href={`/category/${lowCategory.id}`}>
            {lowCategory.name}
          </Link>
        </li>
      ))}
    </ul>
  </ul>
);

const Modal: React.FC<ModalProps> = ({ setMenModalOn, categories }) => (
  <div className="font-[theme-font-content]">
    <div className="fixed top-20 right-0 bottom-0 left-0 bg-black bg-opacity-60 z-50 animate-slidein">
      <div 
        className="relative w-full bg-white flex justify-center z-60"
        onMouseLeave={() => setMenModalOn(false)}
      >
        {groupLowCategories(categories).map((group: CategoryGroupProps, index: number) => (
          <CategoryGroup key={index} group={group} />
        ))}
      </div>
    </div>
  </div>
);

export default Modal;