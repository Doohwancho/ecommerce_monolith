import React, { memo } from 'react';
import { useRecoilState } from 'recoil';
import { isLoggedInState } from '../../store/state';
import styled from 'styled-components';
import { Link } from 'react-router-dom';
import axios from 'axios';

const TopWrapper = styled.div`
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

const TopRight = styled.ul`
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

const Header: React.FC = () => {
    const [isLoggedInUser, setIsLoggedInUser] = useRecoilState(isLoggedInState);

    const logout = () => {
      axios
        .post("http://127.0.0.1:8080/logout", {}, { withCredentials: true })
        .then(() => {
          alert('로그 아웃 성공!');
          setIsLoggedInUser(false); 
        })
        .catch(() => {
          alert('로그 아웃 실패!');
        });
    };

    console.log("Header rendered!");

    return (
      <>
        <TopWrapper>
          <div></div>
          <TopRight>
            {isLoggedInUser ? (
              <li onClick={logout}>로그아웃</li>
            ) : (
              <>
                <Link to="/register">
                  <li>회원가입</li>
                </Link>
                <Link to="/login">
                  <li>로그인</li>
                </Link>
              </>
            )}
          </TopRight>
        </TopWrapper>
      </>
    );
  };

export default memo(Header);
