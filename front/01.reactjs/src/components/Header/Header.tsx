import React, { memo } from 'react';
import { TopWrapper, TopRight } from './styles/Header.styles';
import { useLogout } from './hooks/useLogout';
import { Link } from 'react-router-dom';

const Header: React.FC = () => {
  const { logout, isLoggedInUser } = useLogout();

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
