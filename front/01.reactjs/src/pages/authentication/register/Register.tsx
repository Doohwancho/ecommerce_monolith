import React from 'react';
import { FormContainer, FormTitle, Form, FormGroup, Label, Input, Button, ErrorMessage, LoadingMessage, SuccessMessage } from './styles/Register.styles'
import { useNavigate } from 'react-router-dom';
import useForm from './hooks/useForm';
import useRegisterUser from './hooks/useRegisterUser';
import { initialFormData } from './constants/initialFormData';

const Register: React.FC = () => {
  const { formData, handleChange } = useForm(initialFormData);
  const { mutate, isLoading, isError, error, data } = useRegisterUser();
  const navigate = useNavigate();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    mutate(formData, {
        onSuccess: (responseData) => {
          console.log('Registration Response:', responseData);
          navigate('/login');
        },
        onError: (error) => {
          console.error('Registration Error:', error);
        }
    });
  };

  return (
    <FormContainer>
      <FormTitle>Register</FormTitle>
      {
        <Form onSubmit={handleSubmit}>
            <FormGroup>
              <Label htmlFor="username">Username:</Label>
              <Input
                  type="text"
                  id="username"
                  name="username"
                  value={formData.username}
                  onChange={handleChange}
              />
            </FormGroup>
            <FormGroup>
              <Label htmlFor="email">Email:</Label>
              <Input
                  type="email"
                  id="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
              />
            </FormGroup>
            <FormGroup>
              <Label htmlFor="password">Password:</Label>
              <Input
                  type="password"
                  id="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
              />
            </FormGroup>
            <FormGroup>
              <Label htmlFor="name">Name:</Label>
              <Input
                  type="text"
                  id="name"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
              />
            </FormGroup>
            <FormGroup>
              <Label htmlFor="street">Street:</Label>
              <Input
                  type="text"
                  id="street"
                  name="address.street"
                  value={formData.address.street}
                  onChange={handleChange}
              />
            </FormGroup>
            <FormGroup>
              <Label htmlFor="city">City:</Label>
              <Input
                  type="text"
                  id="city"
                  name="address.city"
                  value={formData.address.city}
                  onChange={handleChange}
              />
            </FormGroup>
            <FormGroup>
              <Label htmlFor="state">State:</Label>
              <Input
                  type="text"
                  id="state"
                  name="address.state"
                  value={formData.address.state}
                  onChange={handleChange}
              />
            </FormGroup>
            <FormGroup>
              <Label htmlFor="country">Country:</Label>
              <Input
                  type="text"
                  id="country"
                  name="address.country"
                  value={formData.address.country}
                  onChange={handleChange}
              />
            </FormGroup>
            <FormGroup>
              <Label htmlFor="zipCode">Zipcode:</Label>
              <Input
                  type="text"
                  id="zipCode"
                  name="address.zipCode"
                  value={formData.address.zipCode}
                  onChange={handleChange}
              />
            </FormGroup>
            <Button type="submit">Register</Button>
        </Form>
    }
    {isLoading && <LoadingMessage>Registering...</LoadingMessage>}
    {isError && <ErrorMessage>Error: {error.message}</ErrorMessage>}
    {data && <SuccessMessage>Registration Successful!</SuccessMessage>}
    </FormContainer>
  );
};

export default Register;
