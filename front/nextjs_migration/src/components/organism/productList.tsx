import { ProductWithOptionsDTO, ProductWithOptionsListResponseDTO } from "../../../models";
import ProductCard from "./productCard";

interface ProductListProps {
  products: ProductWithOptionsListResponseDTO; // Define the type for products
}

const ProductList: React.FC<ProductListProps> = ({ products }) => {
  return (
    <div className="grid gap-8">
      <div className="grid sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-3 gap-6">
        {products.products?.map((product: ProductWithOptionsDTO) => (
          <ProductCard key={product.productId} product={product} /> // Pass each product to ProductCard
        ))}
      </div>
    </div>
  );
};

export default ProductList;
