import React, { useState } from 'react';
import { useMutation, UseMutationResult } from 'react-query';
import { useNavigate } from 'react-router-dom';

interface LoginRequestDTO {
    'username': string;
    'password': string;
}

// Assuming a simple login with username and password
const initialLoginData = {
    username: '',
    password: '',
  };  

const useLoginUser = ():UseMutationResult<any, Error, FormData> => {
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
    });
  };
  

const Login: React.FC = () => {
  const [loginData, setLoginData] = useState<LoginRequestDTO>(initialLoginData);
  const { mutate, isLoading, isError, error } = useLoginUser();
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setLoginData({
      ...loginData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append('username', loginData.username);
    formData.append('password', loginData.password);

    mutate(formData, {
      onSuccess: (response) => {
        console.log('Login Success! Response:', response);

        navigate('/');
      },
      onError: (error) => {
        console.error('Login Failed! Error:', error);
      },
    });
  };

  return (
    <div>
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="username">Username:</label>
          <input
            type="text"
            id="username"
            name="username"
            value={loginData.username}
            onChange={handleChange}
          />
        </div>
        <div>
          <label htmlFor="password">Password:</label>
          <input
            type="password"
            id="password"
            name="password"
            value={loginData.password}
            onChange={handleChange}
          />
        </div>
        <button type="submit">Login</button>
      </form>
      {isLoading && <p>Logging in...</p>}
      {isError && <p>Error: {error.message}</p>}
    </div>
  );
};

export default Login;
