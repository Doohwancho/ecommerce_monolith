'use client';

import React, { useState, useEffect, useCallback } from 'react';
import { ProductWithOptionsListVer2ResponseDTO, ProductWithOptionsVer2DTO, CategoryOptionsOptionVariationsResponseDTO } from '../../../../models';
import TopNavBar from "@/components/organism/topNavBar";
import Footer from "@/components/organism/footer";
import { Button } from "@/components/atom/button";
import ProductFilters from "@/components/organism/productFilters";
import ProductList from "@/components/organism/productList";
import CategoryBar from "@/components/organism/categoryBar";

interface CategoryClientSideComponentProps {
    initialProducts: ProductWithOptionsListVer2ResponseDTO;
    optionsAndOptionVariations: CategoryOptionsOptionVariationsResponseDTO;
    categoryId: string;
}

export interface GroupedProduct {
    productId: number;
    productName: string;
    price: number;
    description: string;
    rating: number;
    ratingCount: number;
    option_variation_id: { [key: number]: number }; // Change this to an object mapping option variation IDs to product item IDs
}

//initial products fetched from server을 product별로 묶고, option_variation별로 묶는다. option_variation을 filter로 쉽게 거르기 위함.
const groupProducts = (products: ProductWithOptionsListVer2ResponseDTO) => {
    const groupedProducts: GroupedProduct[] = []; // Change to an array

    products.products?.forEach((product) => {
        const { productId, productName, price, description, rating, ratingCount, productItemId, optionVariationId, optionVariationName } = product;

        if(optionVariationId !== null) {
            let existingProduct = groupedProducts.find(p => p.productId === productId);
            if (!existingProduct) {
                existingProduct = { 
                    productId, 
                    productName, 
                    price, 
                    description, 
                    rating, 
                    ratingCount, 
                    option_variation_id: {}
                };
                groupedProducts.push(existingProduct); 
            }

            existingProduct.option_variation_id[optionVariationId!] = productItemId; // Use non-null assertion if optionVariationId is optional
        }
    });

    return groupedProducts; // Return the new structure
};

const CategoryClientSideComponent: React.FC<CategoryClientSideComponentProps> = ({ initialProducts, optionsAndOptionVariations, categoryId }) => {
    const [groupedProducts, setGroupedProducts] = useState<GroupedProduct[]>([]); 
    const [filteredProducts, setFilteredProducts] = useState<GroupedProduct[]>([]); 
    const [resetTrigger, setResetTrigger] = useState(false);

    useEffect(() => {
        if (initialProducts) {
            setGroupedProducts(groupProducts(initialProducts)); 
            setFilteredProducts(groupProducts(initialProducts)); 
        } 

    }, [initialProducts]);

    const handleFilterChange = async (newFilters: any) => {
        const filteredProducts = (groupedProducts || []).reduce((acc: GroupedProduct[], groupedProduct) => {
            let matchesPrice = true;
            let matchesOptions = true;

            // Price filter logic
            if (newFilters.priceRanges && newFilters.priceRanges.length > 0) {
                matchesPrice = newFilters.priceRanges.some((range: { min: number; max: number }) => 
                    (groupedProduct.price >= range.min && groupedProduct.price <= range.max)
                );
            }
    
            // Option filter logic
            if (newFilters.selectedOptionVariations && newFilters.selectedOptionVariations.length > 0) {
                matchesOptions = newFilters.selectedOptionVariations.some((variationId: number) => 
                    groupedProduct.option_variation_id.hasOwnProperty(variationId)
                );
            }
    
            // If both conditions match, add to the accumulator
            if (matchesPrice && matchesOptions) {
                acc.push(groupedProduct);
            }
    
            return acc;
        }, []);
    
        setFilteredProducts(filteredProducts); 
    };

    const resetFilters = useCallback(() => {
        setFilteredProducts(groupedProducts);
        setResetTrigger(prev => !prev); // Toggle resetTrigger
    }, [groupedProducts]);

    return (
        <div className="max-w-screen-lg mx-auto">
            <TopNavBar />
            <CategoryBar />

            <section className="bg-white dark:bg-gray-950 py-12">
                <div className="container mx-auto px-4 md:px-6">
                    <div className="flex flex-col md:flex-row items-start md:items-center justify-between mb-8">
                        <div className="grid gap-1">
                            <h2 className="text-2xl font-bold tracking-tight">
                                나이키 트레이닝
                            </h2>
                            <p className="text-gray-500 dark:text-gray-400">
                                한계에 도전하는 여정과 함께할 아이템
                            </p>
                        </div>
                        <Button className="mt-4 md:mt-0 shrink-0" variant="outline" onClick={resetFilters}>
                            <FilterIcon className="w-4 h-4 mr-2" />
                            Reset Filters
                        </Button>
                    </div>

                    <div className="grid md:grid-cols-[240px_1fr] gap-8">
                        <ProductFilters options={optionsAndOptionVariations.options} onFilterChange={handleFilterChange} resetTrigger={resetTrigger} />
                        <ProductList products={filteredProducts} />
                    </div>
                </div>
            </section>

            <Footer />
        </div>
    );
};

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

export default CategoryClientSideComponent;