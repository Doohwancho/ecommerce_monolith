import { useRecoilState } from 'recoil';
import axios from 'axios';
import { isLoggedInState } from '../../../store/state';

export const useLogout = () => {
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

  return { logout, isLoggedInUser };
};