import { useMutation, UseMutationResult } from 'react-query';
import { useSetRecoilState } from 'recoil';
import { isLoggedInState } from '../../../../store/state';
import { useNavigate } from 'react-router-dom';

const useLoginUser = ():UseMutationResult<any, Error, FormData> => {
    const setIsLoggedInUser = useSetRecoilState(isLoggedInState);
    const navigate = useNavigate();

    return useMutation<any, Error, FormData>(async (loginData:FormData) => {
        const baseUrl = 'http://127.0.0.1:8080';
        const endpoint = '/login';
        const fullUrl = baseUrl + endpoint;

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
