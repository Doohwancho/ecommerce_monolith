import Link from "next/link";
import Image from "next/image";
import { GroupedProduct } from "@/app/category/[categoryId]/CategoryClientSideComponent";

interface ProductCardProps {
  product: GroupedProduct; 
}

const formatPrice = (price: number) => {
  return Math.floor(price).toLocaleString(); // Truncate and format with commas
};

const ProductCard: React.FC<ProductCardProps> = ({product}) => {
  if (!product) return null; // Check if product is defined

  return (
        <div className="relative group">
          <Link className="absolute inset-0 z-10" href="#">
            <span className="sr-only">View</span>
          </Link>

          <Image 
            src="/assets/image/category-product-image-1.webp"
            width={300}
            height={300}
            alt="product image"
            />
          <div className="flex-1 py-4">
            <h3 className="font-semibold tracking-tight">
              {product?.productName}
            </h3>
            <small className="text-sm leading-none text-gray-500 dark:text-gray-400">
              {product?.description}
            </small>
            <h4 className="font-semibold">{formatPrice(product?.price)}원</h4>
          </div>
        </div>
  );
};

export default ProductCard;