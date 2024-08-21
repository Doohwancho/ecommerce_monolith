import { useState } from 'react';
import { LoginRequestDTO } from '../types/Login.types';

const useLoginForm = (initialLoginData: LoginRequestDTO) => {
  const [loginData, setLoginData] = useState<LoginRequestDTO>(initialLoginData);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setLoginData({
      ...loginData,
      [e.target.name]: e.target.value,
    });
  };

  return { loginData, handleChange };
};

export default useLoginForm;
