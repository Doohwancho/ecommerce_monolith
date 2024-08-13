'use client'
import React, { useState, useEffect, useRef, memo } from 'react';

import Link from 'next/link';
import { SiNike } from 'react-icons/si';

// import { getUniqueTopCategories, filterCategoriesForTopId } from './util/TopNav.utils';
// import { throttle } from '../util/TopNav.utils';
// import { useScrollHandler } from './hooks/useScrollHandler';

import Modal from '../molecule/modal';

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

const categories = {
    'Men': {
        'topCategoryId':1,
        'topCategoryName':'Men',
        'midCategories': [
            {
                'midCategoryId':4,
                'midCategoryName':"Men's Hat",
                'lowCategories': [
                    {
                        'lowCategoryId':16,
                        'lowCategoryName':'Men-Hat-A'
                    },
                    {
                        'lowCategoryId':17,
                        'lowCategoryName':'Men-Hat-B'
                    },
                    {
                        'lowCategoryId':18,
                        'lowCategoryName':'Men-Hat-C'
                    },
                    {
                        'lowCategoryId':19,
                        'lowCategoryName':'Men-Hat-D'
                    },
                ]
            },
            {
                'midCategoryId':5,
                'midCategoryName':"Men's Top",
                'lowCategories': [
                    {
                        'lowCategoryId':21,
                        'lowCategoryName':'Men-Top-A'
                    },
                    {
                        'lowCategoryId':22,
                        'lowCategoryName':'Men-Top-B'
                    },
                    {
                        'lowCategoryId':23,
                        'lowCategoryName':'Men-Top-C'
                    },
                    {
                        'lowCategoryId':24,
                        'lowCategoryName':'Men-Top-D'
                    },
                ]
            },
            {
                'midCategoryId':6,
                'midCategoryName':"Men's Bottom",
                'lowCategories': [
                    {
                        'lowCategoryId':26,
                        'lowCategoryName':'Men-Bottom-A'
                    },
                    {
                        'lowCategoryId':27,
                        'lowCategoryName':'Men-Bottom-B'
                    },
                    {
                        'lowCategoryId':28,
                        'lowCategoryName':'Men-Bottom-C'
                    },
                    {
                        'lowCategoryId':29,
                        'lowCategoryName':'Men-Bottom-D'
                    },
                ]
            },
            {
                'midCategoryId':7,
                'midCategoryName':"Men's Shoes",
                'lowCategories': [
                    {
                        'lowCategoryId':31,
                        'lowCategoryName':'Men-Shoes-A'
                    },
                    {
                        'lowCategoryId':32,
                        'lowCategoryName':'Men-Shoes-B'
                    },
                    {
                        'lowCategoryId':33,
                        'lowCategoryName':'Men-Shoes-C'
                    },
                    {
                        'lowCategoryId':34,
                        'lowCategoryName':'Men-Shoes-D'
                    },
                ]
            },
        ],
    },
    'Women': {
        'topCategoryId':2,
        'topCategoryName':'Women',
        'midCategories': [
            {
                'midCategoryId':8,
                'midCategoryName':"Women's Hat",
                'lowCategories': [
                    {
                        'lowCategoryId':36,
                        'lowCategoryName':'Women-Hat-A'
                    },
                    {
                        'lowCategoryId':37,
                        'lowCategoryName':'Women-Hat-B'
                    },
                    {
                        'lowCategoryId':38,
                        'lowCategoryName':'Women-Hat-C'
                    },
                    {
                        'lowCategoryId':39,
                        'lowCategoryName':'Women-Hat-D'
                    },
                ]
            },
            {
                'midCategoryId':9,
                'midCategoryName':"Women's Top",
                'lowCategories': [
                    {
                        'lowCategoryId':41,
                        'lowCategoryName':'Women-Top-A'
                    },
                    {
                        'lowCategoryId':42,
                        'lowCategoryName':'Women-Top-B'
                    },
                    {
                        'lowCategoryId':43,
                        'lowCategoryName':'Women-Top-C'
                    },
                    {
                        'lowCategoryId':44,
                        'lowCategoryName':'Women-Top-D'
                    },
                ]
            },
            {
                'midCategoryId':10,
                'midCategoryName':"Women's Bottom",
                'lowCategories': [
                    {
                        'lowCategoryId':46,
                        'lowCategoryName':'Women-Bottom-A'
                    },
                    {
                        'lowCategoryId':47,
                        'lowCategoryName':'Women-Bottom-B'
                    },
                    {
                        'lowCategoryId':48,
                        'lowCategoryName':'Women-Bottom-C'
                    },
                    {
                        'lowCategoryId':49,
                        'lowCategoryName':'Women-Bottom-D'
                    },
                ]
            },
            {
                'midCategoryId':11,
                'midCategoryName':"Women's Shoes",
                'lowCategories': [
                    {
                        'lowCategoryId':51,
                        'lowCategoryName':'Women-Shoes-A'
                    },
                    {
                        'lowCategoryId':52,
                        'lowCategoryName':'Women-Shoes-B'
                    },
                    {
                        'lowCategoryId':53,
                        'lowCategoryName':'Women-Shoes-C'
                    },
                    {
                        'lowCategoryId':54,
                        'lowCategoryName':'Women-Shoes-D'
                    },
                ]
            },
        ]
    },
    'Kids': {
        'topCategoryId':3,
        'topCategoryName':'Kids',
        'midCategories': [
            {
                'midCategoryId':12,
                'midCategoryName':"Kids' Hat",
                'lowCategories': [
                    {
                        'lowCategoryId':56,
                        'lowCategoryName':'Kids-Hat-A'
                    },
                    {
                        'lowCategoryId':57,
                        'lowCategoryName':'Kids-Hat-B'
                    },
                    {
                        'lowCategoryId':58,
                        'lowCategoryName':'Kids-Hat-C'
                    },
                    {
                        'lowCategoryId':59,
                        'lowCategoryName':'Kids-Hat-D'
                    },
                ]
            },
            {
                'midCategoryId':13,
                'midCategoryName':"Kids' Top",
                'lowCategories': [
                    {
                        'lowCategoryId':61,
                        'lowCategoryName':'Kids-Top-A'
                    },
                    {
                        'lowCategoryId':62,
                        'lowCategoryName':'Kids-Top-B'
                    },
                    {
                        'lowCategoryId':63,
                        'lowCategoryName':'Kids-Top-C'
                    },
                    {
                        'lowCategoryId':64,
                        'lowCategoryName':'Kids-Top-D'
                    },
                ]
            },
            {
                'midCategoryId':14,
                'midCategoryName':"Kids' Bottom",
                'lowCategories': [
                    {
                        'lowCategoryId':66,
                        'lowCategoryName':'Kids-Bottom-A'
                    },
                    {
                        'lowCategoryId':67,
                        'lowCategoryName':'Kids-Bottom-B'
                    },
                    {
                        'lowCategoryId':68,
                        'lowCategoryName':'Kids-Bottom-C'
                    },
                    {
                        'lowCategoryId':69,
                        'lowCategoryName':'Kids-Bottom-D'
                    },
                ]
            },
            {
                'midCategoryId':15,
                'midCategoryName':"Kids' Shoes",
                'lowCategories': [
                    {
                        'lowCategoryId':71,
                        'lowCategoryName':'Kids-Shoes-A'
                    },
                    {
                        'lowCategoryId':72,
                        'lowCategoryName':'Kids-Shoes-B'
                    },
                    {
                        'lowCategoryId':73,
                        'lowCategoryName':'Kids-Shoes-C'
                    },
                    {
                        'lowCategoryId':74,
                        'lowCategoryName':'Kids-Shoes-D'
                    },
                ]
            },
        ]
    }
}

const getCategoryData = (categoryName: string): AllCategoriesByDepthResponseDTO[] => {
    const category = categories[categoryName as keyof typeof categories];
    if (!category || !category.midCategories) return [];
  
    return category.midCategories.flatMap(midCategory => 
      midCategory.lowCategories.map(lowCategory => ({
        topCategoryId: category.topCategoryId,
        topCategoryName: category.topCategoryName,
        midCategoryId: midCategory.midCategoryId,
        midCategoryName: midCategory.midCategoryName,
        lowCategoryId: lowCategory.lowCategoryId,
        lowCategoryName: lowCategory.lowCategoryName
      }))
    );
  };

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

const useScrollHandler = () => {
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

  return hide;
};


const CategoryBar3: React.FC = () => {
  const [selectedCategories, setSelectedCategories] = useState<AllCategoriesByDepthResponseDTO[]>([]);
  const [modalOn, setModalOn] = useState(false);
  const hide = useScrollHandler();

  const handleCategoryHover = (categoryName: string) => {
    setModalOn(true);
    setSelectedCategories(getCategoryData(categoryName));
  };

  return (
    <>
      {modalOn && <Modal setMenModalOn={setModalOn} categories={selectedCategories} />}
      <div className="relative w-full">
        <div className={`box-border flex justify-start items-center px-[3vw] relative left-0 h-20 w-full z-50 bg-white ${hide ? 'hidden' : ''} sm:top-0`}>
          <div className="absolute text-6xl">
            <Link href="/" className="text-black hover:text-gray-500">
              <SiNike />
            </Link>
          </div>
          <div className="justify-center mx-auto box-border font-[${props => props.theme.fontContent}] hidden sm:block">
            <div className="flex">
              {/* {getUniqueTopCategories(categories).map((category) => (
                <div 
                  key={category.topCategoryId}
                  className="mx-5 cursor-pointer"
                  onMouseEnter={() => {
                    setModalOn(true);
                    setSelectedCategories(filterCategoriesForTopId(categories, category.topCategoryId));
                  }}
                >
                  {category.topCategoryName}
                </div>
              ))} */}
              {Object.keys(categories).map((categoryName) => (
                <div 
                  key={categoryName}
                  className="mx-5 cursor-pointer"
                  onMouseEnter={() => handleCategoryHover(categoryName)}
                >
                  {categoryName}
                </div>
               ))}
            </div>
          </div>
        </div>
      </div>
    </>
    // <>
    //   {modalOn ? <Modal setMenModalOn={setModalOn} categories={selectedCategories} /> : null}
    //   <TopContainer>
    //     <TopNavWrapper className={hide ? 'hide' : ''}>
    //       <NavLeft>
    //         <Link to="/">
    //           <SiNike />
    //         </Link>
    //       </NavLeft>
    //       <NavCenter>
    //         <div className="mainMenu">
    //           {getUniqueTopCategories(categories).map((category) => (
    //             <div 
    //               key={category.topCategoryId}
    //               onMouseEnter={() => {
    //                 setModalOn(true);
    //                 setSelectedCategories(filterCategoriesForTopId(categories, category.topCategoryId));
    //             }}>
    //                 {category.topCategoryName}
    //             </div>
    //           ))}
    //         </div>
    //       </NavCenter>
    //     </TopNavWrapper>
    //   </TopContainer>
    // </>
  );
}

export default memo(CategoryBar3);
