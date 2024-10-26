'use client'
import React, { useState, useEffect, useRef, } from 'react';
import { AllCategoriesByDepthResponseDTO } from '../../../models';
import { throttle } from "lodash";

import Link from 'next/link';
import { SiNike } from 'react-icons/si';

import Modal from '../molecule/modal';
import TopNavBar from './topNavBar';


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
    const [scrollState, setScrollState] = useState({
      showCategorybar: true,
      isScrollingUp: false
    });
    const [modalOn, setModalOn] = useState(false);
    
    const SCROLL_OFFSET = 30;
    const lastScrollPosition = useRef(0);  // useState 대신 useRef 사용
  
    useEffect(() => {
      // 스크롤 핸들러를 throttle로 감싸서 성능 최적화
      const handleScroll = throttle(() => {
        const currentScroll = window.scrollY;
        const isScrollingUp = currentScroll < lastScrollPosition.current && currentScroll > SCROLL_OFFSET;
        
        setScrollState({
          showCategorybar: currentScroll < SCROLL_OFFSET || isScrollingUp,
          isScrollingUp
        });
        
        lastScrollPosition.current = currentScroll;
      }, 100);  // 100ms마다만 실행
  
      window.addEventListener('scroll', handleScroll);
      return () => {
        window.removeEventListener('scroll', handleScroll);
        handleScroll.cancel();
      };
    }, []); // 의존성 배열이 비어있어 마운트 시에만 실행
  
    const handleCategoryHover = (categoryName: string) => {
        setModalOn(true);
        setSelectedCategories(getCategoryData(categoryName));
    };
  
    return (

    <>
        <nav className="relative h-32">
            <div className={`
                block
                max-w-screen-lg w-full mx-auto 
                z-50 bg-white
                duration-150 ease-out
                ${scrollState.showCategorybar 
                    ? scrollState.isScrollingUp 
                        ? 'fixed top-0'
                        : 'fixed top-0 [transform:translate3d(0,0,0)]'
                        // : 'sticky top-0 [transform:translate3d(0,0,0)]'
                    : 'fixed top-0 left-0 right-0 [transform:translate3d(0,-120px,0)]'
                    // : 'fixed top-0 left-0 right-0 [transform:translate3d(0,-90px,0)]'
                }
                `}>

                <TopNavBar />

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

                <Modal 
                    isVisible={modalOn}
                    showCategorybar={scrollState.showCategorybar}
                    setMenModalOn={setModalOn} 
                    categories={selectedCategories} 
                />
            </div>
        </nav>

        <div className={` 
                fixed
                top-0 bottom-0 
                w-[1024px]

                max-h-full
                overflow-y-auto 
                z-10 
                
                bg-black bg-opacity-40
                ${modalOn 
                    ? 'opacity-100 visible pointer-events-auto'
                    : 'opacity-0 invisible pointer-events-none'
                }
            `}
            style={{ transitionProperty: 'opacity', transitionTimingFunction: 'cubic-bezier(0.4, 0, 0.2, 1);', transitionDuration: '800ms'}} //transition-all duration-800 ease-in-out 입력에서 duration이 800ms로 적용 안되서 이 방식으로 대신 입력함 
        >
        </div>
    </>
    );
  };

export default CategoryBar;
