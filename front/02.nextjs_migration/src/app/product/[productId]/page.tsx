import { Button } from "@/components/atom/button";
import Image from "next/image";
import Footer from "@/components/organism/footer";
import TopNavBar from "@/components/organism/topNavBar";
import CategoryBar from "@/components/organism/categoryBar";
import { ProductDetailResponseDTO } from '../../../../models';

interface ProductPageProps {
  params: {
    productId: string;
  };
}

const fetchProductById = async (productId: string): Promise<ProductDetailResponseDTO[]> => {
  const BASE_URL = process.env.NEXT_PUBLIC_API_URL;
  if (!BASE_URL) {
    console.error('NEXT_PUBLIC_API_URL is not set');
    throw new Error('API URL is not configured');
  }
  const endpoint = `/products/${productId}`;
  const fullUrl = BASE_URL + endpoint;

  try {
    const res = await fetch(fullUrl);
    if (!res.ok) {
      throw new Error(`HTTP error! status: ${res.status}`);
    }
    return res.json();
  } catch (error) {
    console.error('Error fetching product:', error);
    throw error;
  }
};

const formatPrice = (price: number) => {
  return Math.floor(price).toLocaleString(); // Truncate and format with commas
};

function getFirstDigitForRandomProductImageIndex(number: number) {
  // 숫자를 문자열로 변환
  const numStr = Math.abs(number).toString();
  
  // 문자열의 첫 번째 문자를 숫자로 변환하여 반환
  return parseInt(numStr[0]);
}

const ProductPage = async ({ params }: ProductPageProps) => {
  const { productId } = params;

  try {
    const product = await fetchProductById(productId);
    //TODO - product details info들을 product_item/option 별로 묶어서, discounted price 표시

    
  return (
    <div className="max-w-screen-lg mx-auto">
      {/* <TopNavBar /> */}
      <CategoryBar />

      {/* body */}
      <div className="max-w-4xl mx-auto py-12 px-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div className="grid grid-cols-2 gap-4">
            {/* <img src="/placeholder.svg" alt="Product image" className="col-span-2" /> */}
            {/* <img src="/placeholder.svg" alt="Product image thumbnail" /> */}
            {/* <img src="/placeholder.svg" alt="Product image thumbnail" /> */}
            <Image
              src={`/assets/image/category-product-image-${getFirstDigitForRandomProductImageIndex(product[0].productId)}-2.webp`}
              width={300}
              height={300}
              layout="responsive"
              objectFit="cover"
              alt="product image"
            />
            <Image
              src={`/assets/image/category-product-image-${getFirstDigitForRandomProductImageIndex(product[0].productId)}-3.webp`}
              width={300}
              height={300}
              layout="responsive"
              objectFit="cover"
              alt="product image"
            />
            <Image
              src={`/assets/image/category-product-image-${getFirstDigitForRandomProductImageIndex(product[0].productId)}-4.webp`}
              width={300}
              height={300}
              layout="responsive"
              objectFit="cover"
              alt="product image"
            />
            <Image
              src={`/assets/image/category-product-image-${getFirstDigitForRandomProductImageIndex(product[0].productId)}-5.webp`}
              width={300}
              height={300}
              layout="responsive"
              objectFit="cover"
              alt="product image"
            />
          </div>
          <div>
            <div className="flex justify-between items-start">
              <h1 className="text-4xl font-bold">{product[0].name}</h1>
              <div className="flex items-center space-x-2">
                <HeartIcon className="h-6 w-6" />
                <ShareIcon className="h-6 w-6" />
              </div>
            </div>
            <p className="text-2xl font-semibold my-4">{formatPrice(product[0].price)}원</p>
            <p className="text-gray-600 mb-4">
              {product[0].description}
            </p>
            <p className="text-gray-600 mb-4">by Vendor Name</p>
            <div className="mb-4">
              <p className="font-semibold mb-2">Size</p>
              <div className="flex space-x-2">
                <Button variant="outline">S</Button>
                <Button variant="outline">M</Button>
                <Button variant="outline">L</Button>
              </div>
            </div>
            <div className="flex items-center justify-between mb-4">
              <Button>Add to Cart</Button>
              <div className="flex items-center space-x-2">
                <Button variant="ghost">-</Button>
                <span>1</span>
                <Button variant="ghost">+</Button>
              </div>
            </div>
            <div className="flex space-x-4 text-sm text-gray-600">
              <span>Free standard shipping</span>
              <span>Free Returns</span>
            </div>
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
} catch (error) {
  console.error("Error fetching product:", error);
  return <div>Error loading product. Please try again later.</div>;
}
}

export const dynamic = 'force-dynamic'; // Ensure SSR


function HeartIcon(props: any) {
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
      <path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z" />
    </svg>
  );
}



function ShareIcon(props: any) {
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
      <path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8" />
      <polyline points="16 6 12 2 8 6" />
      <line x1="12" x2="12" y1="2" y2="15" />
    </svg>
  );
}

export default ProductPage;
