import React from 'react';
import Link from 'next/link';
import { AllCategoriesByDepthResponseDTO } from '../../../models';

  interface ModalProps {
    isVisible: boolean;
    showCategorybar: boolean;
    setMenModalOn: (value: boolean) => void;
    categories: AllCategoriesByDepthResponseDTO[];
  }
  
  interface LowCategory {
    id: number | string;
    name: string;
  }
  
  interface CategoryGroup {
    midCategoryName: string;
    lowCategories: LowCategory[];
  }

  //이 유틸함수 역시 모달에서만 쓰는 함수니까 따로 분리하지 말자. 
  const groupLowCategories = (categories: AllCategoriesByDepthResponseDTO[]): CategoryGroup[] => {
    const grouped: { [key: string]: CategoryGroup } = {};
  
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
const CategoryGroup: React.FC<{ group: CategoryGroup; isVisible: boolean }> = ({ group, isVisible }) => (
  <ul className={`p-10
    transition-all duration-700 ease-out
    ${isVisible 
      ? 'opacity-100 visible  text-gray-900' 
      : 'opacity-0 invisible text-gray-200'
    }
  `}>
    <li className="mb-4 font-bold text-lg">{group.midCategoryName}</li>
    <ul className="mt-0">
      {group.lowCategories.map((lowCategory: LowCategory, subIndex: number) => (
        <li key={subIndex} className="mb-2 text-sm hover:text-gray-900">
          <Link href={`/category/${lowCategory.id}`}>
            {lowCategory.name}
          </Link>
        </li>
      ))}
    </ul>
  </ul>
);

const Modal: React.FC<ModalProps> = ({ isVisible, showCategorybar, setMenModalOn, categories }) => {

  return (
      <div 
        className={`
          block
          sticky 
          top-0
          left-0 right-0 bottom-0
          z-[50]

          transition-all duration-200 ease-in

          ${isVisible
            ? 'opacity-100 visible h-auto'
            : 'opacity-0 invisible h-0'
          }
          `
      }
      >
        {/* Modal Content */}
        <div 
          className={`
            fixed w-full left-0 bg-white flex justify-center z-[60] border-0 p-0 m-0
            
            transform transition-transform duration-200 ease-out
            ${isVisible 
              ? 'translate-y-0 opacity-100'
              : '-translate-y-40 opacity-0'
            }
          `}
          onMouseLeave={() => setMenModalOn(false)}
        >
          {groupLowCategories(categories).map((group: CategoryGroup, index: number) => (
            <CategoryGroup key={index} group={group} isVisible={isVisible}/>
          ))}
        </div>
      </div>
  );
};

export default Modal;