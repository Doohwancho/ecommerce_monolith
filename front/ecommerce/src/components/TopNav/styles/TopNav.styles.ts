import styled from 'styled-components';


const TopContainer = styled.div`
  position: relative;
  width: 100%;
`;

const TopNavWrapper = styled.div`
  box-sizing: border-box;
  display: flex;
  justify-content: flex-start; 
  align-items: center;
  padding: 0 3vw;
  position: relative;
  left: 0;
  height: 80px;
  width: 100%;
  font-family: ${props => props.theme.fontContent};
  z-index: 100;
  background-color: white;

  @media screen and (max-width: 640px) {
    top: 0;
  }
`;

const NavLeft = styled.div`
  position: absolute;
  font-size: 3.5rem;

  a {
    color: black;

    & :hover {
      color: gray;
    }
  }
`;

const NavCenter = styled.div`
  justify-content: center;
  margin: 0 auto; 
  box-sizing: border-box;
  font-family: ${props => props.theme.fontContent};

  a {
    color: black;
    text-decoration: none;
  }

  .mainMenu {
    display: flex;

    & div {
      margin: 0 20px;
      cursor: pointer;
    }
  }

  @media screen and (max-width: 640px) {
    display: none;
  }
`;

export { TopContainer, TopNavWrapper, NavLeft, NavCenter };