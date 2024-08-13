import React from 'react';
import Link from 'next/link';

export interface AllCategoriesByDepthResponseDTO {
    /**
     * 
     * @type {number}
     * @memberof AllCategoriesByDepthResponseDTO
     */
    'topCategoryId'?: number;
    /**
     * 
     * @type {string}
     * @memberof AllCategoriesByDepthResponseDTO
     */
    'topCategoryName'?: string;
    /**
     * 
     * @type {number}
     * @memberof AllCategoriesByDepthResponseDTO
     */
    'midCategoryId'?: number;
    /**
     * 
     * @type {string}
     * @memberof AllCategoriesByDepthResponseDTO
     */
    'midCategoryName'?: string;
    /**
     * 
     * @type {number}
     * @memberof AllCategoriesByDepthResponseDTO
     */
    'lowCategoryId'?: number;
    /**
     * 
     * @type {string}
     * @memberof AllCategoriesByDepthResponseDTO
     */
    'lowCategoryName'?: string;
}


export interface ModalProps {
    setMenModalOn: (value: boolean) => void;
    categories: AllCategoriesByDepthResponseDTO[];
  }
  
  export interface LowCategory {
    id: number | string;
    name: string;
  }
  
  export interface CategoryGroupProps {
    midCategoryName: string;
    lowCategories: LowCategory[];
  }

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
  

const CategoryGroup: React.FC<{ group: CategoryGroupProps }> = ({ group }) => (
  <ul className="p-10">
    <li className="mb-4 font-bold text-lg text-black">{group.midCategoryName}</li>
    <ul className="mt-0">
      {group.lowCategories.map((lowCategory: LowCategory, subIndex: number) => (
        <li key={subIndex} className="mb-2 text-sm text-gray-600 hover:text-gray-900">
          <Link href={`/products/category/${lowCategory.id}`}>
            {lowCategory.name}
          </Link>
        </li>
      ))}
    </ul>
  </ul>
);

const Modal: React.FC<ModalProps> = ({ setMenModalOn, categories }) => (
  <div className="font-[theme-font-content]">
    <div className="fixed top-20 right-0 bottom-0 left-0 bg-black bg-opacity-60 z-100 animate-slidein">
      <div 
        className="relative w-full bg-white flex justify-center"
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