import styled from 'styled-components';

const FooterWrapper = styled.div`
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  font-family: ${props => props.theme.fontContent};
  background-color: black;
  color: gray;
  font-size: 11px;
  /* position: fixed; */
  /* bottom: 0; */
  width: 100%;
`;

const FooterTop = styled.div`
  display: flex;
  justify-content: space-between;
  width: 900px;
  padding: 50px 10px;

  li {
    padding: 5px;
  }

  .rowTitle {
    font-size: 13px;
    color: white;
  }

  .firstRow {
    color: white;
    font-size: 13px;
  }

  .icon {
    font-size: 15px;
    margin: 5px;
  }

  .mobileRow {
    display: none;
    color: white;
  }

  @media screen and (max-width: 640px) {
    padding: 20px;

    .mobileRow {
      display: flex;
      justify-content: center;
      width: 100%;

      div {
        padding: 10px;
      }
    }

    .firstRow,
    .secondRow,
    .thirdRow,
    .forthRow {
      display: none;
    }
  }
`;

const FooterBody = styled.div`
  display: flex;
  justify-content: space-between;
  width: 100%;
  border-top: 0.5px solid gray;
  border-bottom: 0.5px solid gray;

  .bodyWrapper {
    display: flex;
    padding: 10px 3vw;

    .left {
      margin-right: 10px;
    }
    .right {
      margin-left: 10px;
    }
  }

  @media screen and (max-width: 640px) {
    flex-direction: column;
    align-items: center;
    padding: 10px;
  }
`;
const FooterBottom = styled.div`
  display: flex;
  justify-content: center;
  /* width: 900px; */

  span {
    color: white;
    padding-left: 2px;
  }
  .footerWrapper {
    margin: 30px 100px;

    div {
      padding-bottom: 5px;
    }

    .endRow {
      padding-bottom: 0px;
    }
  }

  @media screen and (max-width: 640px) {
    flex-direction: column;
  }
`;

export {FooterWrapper, FooterTop, FooterBody, FooterBottom};