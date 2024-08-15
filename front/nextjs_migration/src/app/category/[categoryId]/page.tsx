import React from 'react';
import { useRouter } from 'next/router';
import { ProductWithOptionsListResponseDTO } from '../../../../models';

import TopNavBar from "@/components/organism/topNavBar";
import Footer from "@/components/organism/footer";
import { Button } from "@/components/atom/button";
import ProductFilters from "@/components/organism/productFilters";
import ProductList from "@/components/organism/productList";
import CategoryBar from "@/components/organism/categoryBar";

interface CategoryPageProps {
    params: {
        categoryId: string;
    };
}
  

const fetchProductsWithOptionsByCategoryId = async (categoryId: string): Promise<ProductWithOptionsListResponseDTO> => {
    const BASE_URL= process.env.NEXT_PUBLIC_API_URL;
    const endpoint = `/products/category/${categoryId}`;
    const fullUrl = BASE_URL + endpoint;

    const response = await fetch(fullUrl, { credentials: 'include' });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
};

const CategoryPage: React.FC<CategoryPageProps> = async ({ params }) => {
  const { categoryId } = params;
  const response = await fetchProductsWithOptionsByCategoryId(categoryId);

  return (
    <>
        <div className="max-w-screen-lg mx-auto">
            <TopNavBar />
            <CategoryBar />

            <h2 className="text-2xl font-bold tracking-tight">
                Category ID: {categoryId}
                what: {response.products?.length}
            </h2>
    
            {/* body */}
            <section className="bg-white dark:bg-gray-950 py-12">
            <div className="container mx-auto px-4 md:px-6">
                {/* chunk1 - reset filters */}
                <div className="flex flex-col md:flex-row items-start md:items-center justify-between mb-8">
                <div className="grid gap-1">
                    <h2 className="text-2xl font-bold tracking-tight">
                    나이키 트레이닝
                    </h2>
                    <p className="text-gray-500 dark:text-gray-400">
                    한계에 도전하는 여정과 함께할 아이템
                    </p>
                </div>
                <Button className="mt-4 md:mt-0 shrink-0" variant="outline">
                    <FilterIcon className="w-4 h-4 mr-2" />
                    Reset Filters
                </Button>
                </div>
    
                <div className="grid md:grid-cols-[240px_1fr] gap-8">
                {/* chunk2 - filters */}
                <ProductFilters />
                {/* chunk3 - productlist */}
                <ProductList />
                </div>
            </div>
            </section>
    
            <Footer />
      </div>
      </>
  );
};

export const dynamic = 'force-dynamic'; // Ensure SSR

function FilterIcon(props: any) {
  return (
    <svg
      {...props}
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3" />
    </svg>
  );
}



export default CategoryPage;
