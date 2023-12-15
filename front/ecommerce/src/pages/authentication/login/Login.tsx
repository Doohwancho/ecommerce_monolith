import React from 'react';
import { FormContainer, FormTitle, Form, FormGroup, Label, Input, Button, ErrorMessage } from './styles/Login.styles'
import { initialLoginData } from './constants/Login.constants'
import useLoginForm from './hooks/useLoginForm';
import useLoginUser from './hooks/useLoginUser';


const Login: React.FC = () => {
  const { loginData, handleChange } = useLoginForm(initialLoginData);
  const { mutate, isLoading, isError, error } = useLoginUser();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append('username', loginData.username);
    formData.append('password', loginData.password);

    mutate(formData);
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
