import { CSSProperties } from "react";
import React from "react";

import { StyledSlider, StyledLink, Card, ProductImage, ProductDetails, ProductCategory, ProductTitle, ProductDescription } from './styles/CarouselComponent.styles';
import "slick-carousel/slick/slick.css"; 
import "slick-carousel/slick/slick-theme.css";
import { FaArrowRight, FaArrowLeft } from "react-icons/fa"; // Using react-icons for arrow icons
import { useQuery } from 'react-query';
import { ProductDTO } from "../../../../../../models/src/model/product-dto";
import { fetchTopRatedProducts } from './service/CarousalService'; 

const CarouselComponent: React.FC = () => {
    const settings = {
      dots: true,
      infinite: true,
      speed: 500,
      slidesToShow: 3,
      slidesToScroll: 1,
      arrows: true, 
      centerMode: true,
    //   centerPadding: "0px",
      autoplay: true, 
    //   autoplaySpeed: 2000, //자동으로 넘어가는 속도
      nextArrow: <NextArrow />,
      prevArrow: <PrevArrow />
    };

    const { data: topRatedProducts, status } = useQuery(
        ['fetchTopRatedProducts', "fetchTopRatedProducts"], 
        () => fetchTopRatedProducts()
    );

    // Render each product as a slide
    const renderSlides = () => topRatedProducts?.map((product: ProductDTO) => (
        <StyledLink to={`/products/${product.productId}`} key={product.productId}>
            <Card>
                <ProductImage src="/images/category-product-image-1.webp" alt="" />
                <ProductDetails>
                    <ProductCategory>{product.categoryId}</ProductCategory>
                    <ProductTitle>{product.name.substring(0, 20)}</ProductTitle>
                    <ProductDescription>{product.description.substring(0, 25)}</ProductDescription>
                </ProductDetails>
            </Card>
        </StyledLink>
    ));

    return (
      <div style={{ width: '80%', margin: 'auto' }}>
        <StyledSlider {...settings}>
            {status === 'success' && renderSlides()}
        </StyledSlider>
      </div>
    );
  };

interface ArrowProps {
  className?: string;
  style?: CSSProperties;
  onClick?: () => void;
}

const NextArrow: React.FC<ArrowProps> = ({ className, style, onClick }) => {
    return (
      <div
        className={className}
        style={{ ...style, display: "block", background: "none" }}
        onClick={onClick}
      >
        <FaArrowRight />
      </div>
    );
  };
  
  const PrevArrow: React.FC<ArrowProps> = ({ className, style, onClick }) => {
    return (
      <div
        className={className}
        style={{ ...style, display: "block", background: "none" }}
        onClick={onClick}
      >
        <FaArrowLeft />
      </div>
    );
  };


  export default CarouselComponent;  