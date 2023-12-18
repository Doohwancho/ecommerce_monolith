import { useMutation, UseMutationResult } from 'react-query';
import { useSetRecoilState } from 'recoil';
import { isLoggedInState } from '../../../../store/state';
import { useNavigate } from 'react-router-dom';

const useLoginUser = ():UseMutationResult<any, Error, FormData> => {
    const setIsLoggedInUser = useSetRecoilState(isLoggedInState);
    const navigate = useNavigate();

    return useMutation<any, Error, FormData>(async (loginData:FormData) => {
        const BASE_URL = import.meta.env.VITE_API_BASE_URL;
        const endpoint = '/login';
        const fullUrl = BASE_URL + endpoint;

      const response = await fetch(fullUrl, {
        method: 'POST',
        body: loginData,
        credentials: 'include'
      });

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
  
      return response;
    }, {
        onSuccess: (response) => {
            const loginStatusHeader = response.headers.get('Login-Status');
            if (loginStatusHeader === 'success') {
              setIsLoggedInUser(true);
              navigate('/');
            }
            // Handle other statuses if necessary
          },
          onError: (error) => {
            console.error('Login Failed! Error:', error);
          }
    });
  };

export default useLoginUser;
