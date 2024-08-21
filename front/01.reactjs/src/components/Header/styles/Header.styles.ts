// Header.styles.ts
import styled from 'styled-components';

export const TopWrapper = styled.div`
  box-sizing: border-box;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f2f2f2;
  padding: 0.5vh 3vw;

  @media screen and (max-width: 640px) {
    display: none;
  }
`;

export const TopRight = styled.ul`
  display: flex;

  li {
    padding-left: 1vw;
    font-size: 0.8rem;
    cursor: pointer;
  }

  a {
    text-decoration: none;
    color: black;
  }
`;