import styled from 'styled-components';
import { Link } from 'react-router-dom';

import Slider from "react-slick";


const StyledSlider = styled(Slider)`
    .slick-prev:before, .slick-next:before {
        color: darkgrey;
    }
`;
  
const StyledLink = styled(Link)`
  text-decoration: none; // Removes underline from links
  flex: 0 0 calc(33.333% - 20px); // Adjust width to fit three items per row
  box-shadow: 0 2px 7px #dfdfdf;
  margin: 10px; // Adjust margin for spacing
  background: #fafafa;
`;

const Card = styled.div`
  color: inherit;
`;

const ProductImage = styled.img`
    max-width: 100%;
    max-height: 100%;
`;

const ProductDetails = styled.div`
    padding: 30px;
`;

const ProductCategory = styled.span`
    display: block;
    font-size: 12px;
    font-weight: 700;
    text-transform: uppercase;
    color: #ccc;
    margin-bottom: 18px;
`;

const ProductTitle = styled.h4`
    font-weight: 500;
    text-transform: uppercase;
    color: #363636;
    margin-bottom: 18px;
    &:hover {
        color: #fbb72c;
    }
`;

const ProductDescription = styled.p`
    font-size: 15px;
    line-height: 22px;
    margin-bottom: 18px;
    color: #999;
`;

export { StyledSlider, StyledLink, Card, ProductImage, ProductDetails, ProductCategory, ProductTitle, ProductDescription };