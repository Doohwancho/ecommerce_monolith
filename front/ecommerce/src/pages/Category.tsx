import React, { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from 'react-query';
import { useRecoilValue } from 'recoil';
import { categoriesState } from '../store/state'; // Adjust the import path as needed
import { AllCategoriesByDepthResponseDTO } from 'model';



const Category = () => {
  const { categoryName } = useParams();
  const categories = useRecoilValue(categoriesState);

  useEffect(() => {
    console.log("Updated categories:", categories);
  }, [categories]); 

//   const { data, isLoading, error } = useQuery(['categoryData', categoryName], () =>
//     fetch(`http://your-api-endpoint/categories/${categoryName}`).then(res => res.json())
//   );

//   if (isLoading) return <div>Loading...</div>;
//   if (error) return <div>Error: {error.message}</div>;

  return (
    <div>
        <h1>Top Categories</h1>
        {categories && categories.length > 0 ? (
          <ul>
            {categories.map((category: AllCategoriesByDepthResponseDTO, index: number) => (
              <li key={index}>
                Top: {category.topCategoryName} (ID: {category.topCategoryId}),
                Mid: {category.midCategoryName} (ID: {category.midCategoryId}),
                Low: {category.lowCategoryName} (ID: {category.lowCategoryId})
              </li>
            ))}
          </ul>
        ) : (
          <p>No categories available.</p>
        )}
    
        <h1>Category: {categoryName}</h1>
        {/* Render category data */}
    </div>
  );
};

export default Category;
