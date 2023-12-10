import React, { useState } from 'react';
import { useMutation, UseMutationResult } from 'react-query';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';

// Reuse the styled components from the Register component
const FormContainer = styled.div`
  display: grid;
  place-items: center;
  height: 100vh;
  background-color: #f7f7f7;
`;

const FormTitle = styled.h2`
  font-size: 2rem;
  color: #333;
  // margin-bottom: 20px;
  text-align: center;
  text-transform: uppercase;
  letter-spacing: 1.5px;
`;

const Form = styled.form`
  display: grid;
  grid-gap: 15px;
  width: 100%;
  max-width: 400px;
  padding: 20px;
  border-radius: 8px;
  background: white;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
`;

const FormGroup = styled.div`
  display: flex;
  flex-direction: column;
`;

const Label = styled.label`
  font-size: 0.9rem;
  color: #555;
  margin-bottom: 5px;
`;

const Input = styled.input`
  padding: 10px;
  border: 2px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  &:focus {
    border-color: #a4c9f3;
    outline: none;
  }
`;

const Button = styled.button`
  padding: 10px;
  background-color: #0056b3;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  &:hover {
    background-color: #003d82;
  }
`;

const ErrorMessage = styled.p`
  color: red;
`;

// const LoadingMessage = styled.p`
//   color: #0275d8; // Bootstrap's info color
//   font-size: 1rem;
//   text-align: center;
// `;

interface LoginRequestDTO {
    'username': string;
    'password': string;
}

// Assuming a simple login with username and password
const initialLoginData = {
    username: 'test-id',
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
        const loginStatusHeader = response.headers.get('Login-Status');
        if(loginStatusHeader == 'success') {
          console.log('Login Success! Response:', response);
          navigate('/');
        }
        else if(loginStatusHeader == 'failed') {
          console.log('login failed!', response);
        }
      },
      onError: (error) => {
        console.error('Login Failed! Error:', error);
      },
    });
  };

  return (
    <FormContainer>
      <Form onSubmit={handleSubmit}>
        <FormTitle>Login</FormTitle>
        <FormGroup>
          <Label htmlFor="username">Username:</Label>
          <Input type="text" id="username" name="username" value={loginData.username} onChange={handleChange} />
        </FormGroup>
        <FormGroup>
          <Label htmlFor="password">Password:</Label>
          <Input type="password" id="password" name="password" value={loginData.password} onChange={handleChange} />
        </FormGroup>
        <Button type="submit">Login</Button>
      </Form>
      {/* {isLoading && <LoadingMessage>Logging in...</LoadingMessage>} */}
      {isError && <ErrorMessage>Error: {error.message}</ErrorMessage>}
    </FormContainer>
  );
};

export default Login;
