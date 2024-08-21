import { GroupedProduct } from "@/app/category/[categoryId]/CategoryClientSideComponent";
import { ProductWithOptionsVer2DTO } from "../../../models";
import ProductCard from "./productCard";

interface ProductListProps {
  products: ProductWithOptionsVer2DTO[] | GroupedProduct[] | undefined; // Define the type for products
}

const ProductList: React.FC<ProductListProps> = ({ products }) => {
  return (
    <div className="grid gap-8">
      <div className="grid sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-3 gap-6">
        {products?.map((product, index) => {
          // Type guard to check if product is ProductWithOptionsVer2DTO
          if ('productId' in product) {
            return <ProductCard key={`${product.productId}-${index}`} product={product} />; // Pass each product to ProductCard
          }
          // Handle GroupedProduct case if needed
          return null; // or another component for GroupedProduct
        })}
      </div>
    </div>
  );
};

export default ProductList;
