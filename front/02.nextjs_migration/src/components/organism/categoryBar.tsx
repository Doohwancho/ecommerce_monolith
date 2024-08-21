'use client'
import React, { useState, useEffect, memo } from 'react';
import { AllCategoriesByDepthResponseDTO } from '../../../models';

import Link from 'next/link';
import { SiNike } from 'react-icons/si';

import Modal from '../molecule/modal';


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

const CategoryBar: React.FC = () => {
  const [selectedCategories, setSelectedCategories] = useState<AllCategoriesByDepthResponseDTO[]>([]);
  const [modalOn, setModalOn] = useState(false);
  const [scrollY, setScrollY] = useState(0);

  useEffect(() => {
    const handleScroll = () => {
      setScrollY(window.scrollY);
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const handleCategoryHover = (categoryName: string) => {
    setModalOn(true);
    setSelectedCategories(getCategoryData(categoryName));
  };

  return (
    <>
      {modalOn && <Modal setMenModalOn={setModalOn} categories={selectedCategories} scrollY={scrollY} />}
      <div className="relative w-full">
        <div className="box-border flex justify-start items-center px-[3vw] h-20 w-full">
          <div className="absolute text-6xl">
            <Link href="/" className="text-black hover:text-gray-500">
              <SiNike />
            </Link>
          </div>
          <div className="justify-center mx-auto box-border font-[${props => props.theme.fontContent}] hidden sm:block">
            <div className="flex">
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
  );
}

export default memo(CategoryBar);
