import Link from "next/link";
import Image from "next/image";
import { GroupedProduct } from "@/app/category/[categoryId]/CategoryClientSideComponent";

interface ProductCardProps {
  product: GroupedProduct; 
}

const formatPrice = (price: number) => {
  return Math.floor(price).toLocaleString(); // Truncate and format with commas
};

function getFirstDigitForRandomProductImageIndex(number: number) {
  // 숫자를 문자열로 변환
  const numStr = Math.abs(number).toString();
  
  // 문자열의 첫 번째 문자를 숫자로 변환하여 반환
  return parseInt(numStr[0]);
}

const ProductCard: React.FC<ProductCardProps> = ({product}) => {
  if (!product) return null; // Check if product is defined

  return (
        <Link href={`/product/${product.productId}`} className="relative group">
          <div className="absolute inset-0 z-10">
            <span className="sr-only">View</span>
          </div>

          <Image 
            src={`/assets/image/category-product-image-${getFirstDigitForRandomProductImageIndex(product.productId)}-1.webp`}
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
      </Link>
  );
};

export default ProductCard;