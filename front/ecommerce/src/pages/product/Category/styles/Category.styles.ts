import styled from 'styled-components';
import { Link } from 'react-router-dom';

const MainContainer = styled.div`
    display: flex;
    // width: 100%;
    width: 100vw;
`;

const Area = styled.div`
    flex: ${props => props.className === 'left-area' ? '0 0 15vw' : 'flex'};
    flex-wrap: wrap;
    justify-content: space-around;
    // padding: 20px;
`;

//left area for filter bar
const Container = styled.div`
    width: 100%;
    padding: 10px;
    box-sizing: border-box;

    border-top: 1px solid #ccc;
`;

const Title = styled.div`
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 15px;
`;

const CheckboxList = styled.div`
    display: flex;
    flex-direction: column;
`;

const CheckboxItem = styled.div`
    margin-bottom: 10px;

    input[type="checkbox"] {
        margin-right: 5px;
    }
`;

//right area for product cards
const ProductContainer = styled.div`
  display: flex;
  flex-wrap: wrap;
  // width: 100%;
  width: 82vw;
  // justify-content: space-around;
  justify-content: flex-start; // Adjusted for alignment
  padding: 10px; // Optional, for some spacing around the container
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

const Badge = styled.div`
    position: absolute;
    left: 0;
    top: 20px;
    text-transform: uppercase;
    font-size: 13px;
    font-weight: 700;
    background: red;
    color: #fff;
    padding: 3px 10px;
`;

const ProductThumb = styled.div`
    display: flex;
    align-items: center;
    justify-content: center;
    height: 300px;
    padding: 50px;
    background: #f0f0f0;
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

const ProductBottomDetails = styled.div`
    overflow: hidden;
    border-top: 1px solid #eee;
    padding-top: 20px;
`;

const ProductPrice = styled.div`
    float: left;
    width: 50%;
    font-size: 18px;
    color: #fbb72c;
    font-weight: 600;
`;

const ProductPriceSmall = styled.small`
    font-size: 80%;
    font-weight: 400;
    text-decoration: line-through;
    display: inline-block;
    margin-right: 5px;
`;

const ProductLinks = styled.div`
    text-align: right;
    float: left;
    width: 50%;
`;

const ProductLink = styled.a`
    display: inline-block;
    margin-left: 5px;
    color: #e1e1e1;
    transition: 0.3s;
    font-size: 17px;
    &:hover {
        color: #fbb72c;
    }
`;

export {MainContainer, Area, Container, Title, CheckboxList, CheckboxItem, ProductContainer, StyledLink, Card, Badge, ProductThumb, ProductImage, ProductDetails, ProductCategory, ProductTitle, ProductDescription, ProductBottomDetails, ProductPrice, ProductPriceSmall, ProductLinks, ProductLink};