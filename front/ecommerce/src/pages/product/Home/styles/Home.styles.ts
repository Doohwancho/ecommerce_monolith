import styled from 'styled-components';

const MainWrapper = styled.div`
  margin: 3vw;
  max-width: 100vw;
  font-family: ${({ theme }) => theme.fontContent};

  .pc {
    display: block;
  }

  .mobile {
    display: none;
  }

  .title {
    margin: 30px;
    font-size: 50px;
    font-weight: 900;
  }

  .content {
    margin: 5px;
  }

  button {
    margin: 30px;
    background-color: black;
    color: white;
    padding: 14px 36px;
    border: none;
    border-radius: 20px;
    font-size: 14px;
  }

  @media screen and (max-width: 640px) {
    .pc {
      display: none;
    }
    .mobile {
      display: block;
    }
  }
`;

const MainElement = styled.div`
  width: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;

  img {
    max-width: 100%;
    height: auto;
    display: block;
  }
`;

const MainContent = styled.div`
  margin-bottom: 60px;
  text-align: center;
`;


export {MainWrapper, MainElement, MainContent};