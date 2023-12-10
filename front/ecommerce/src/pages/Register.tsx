import React, { useState } from 'react';
import { useMutation, UseMutationResult } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { RegisterRequestDTO, RegisterResponseDTO } from 'model';
import styled from 'styled-components';


// Styled Components
const FormContainer = styled.div`
  display: grid;
  place-items: center;
  min-height: 100vh;
  background-color: #f7f7f7;
`;

const FormTitle = styled.h2`
  font-size: 2rem;
  color: #333;
  margin-top: 20px;
  margin-bottom: 20px;
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

const LoadingMessage = styled.p`
  color: #0275d8; // Bootstrap's info color
  font-size: 1rem;
  text-align: center;
`;

const SuccessMessage = styled.p`
  color: #5cb85c; // Bootstrap's success color
  font-size: 1rem;
  text-align: center;
`;


const initialFormData = {
  username: 'test-id',  // Default value for username
  email: 'default@example.com',  // Default value for email
  password: '',  // Default value for password
  name: 'John Doe',  // Default value for name
  address: {
    street: '123 Main St',  // Default value for street
    city: 'Metropolis',  // Default value for city
    state: 'StateName',  // Default value for state
    country: 'CountryName',  // Default value for country
    zipCode: '12345',  // Default value for zip code
  },
};


const useRegisterUser = ():UseMutationResult<RegisterResponseDTO, Error, RegisterRequestDTO> => {
    return useMutation<RegisterResponseDTO, Error, RegisterRequestDTO>(async (formData:RegisterRequestDTO) => {

        const baseUrl = 'http://127.0.0.1:8080';
        const endpoint = '/register';
        const fullUrl = baseUrl + endpoint;

      const response = await fetch(fullUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
        credentials: 'include'
      });
  
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
  
      return response.json();
    });
  };

const Register: React.FC = () => {
  const [formData, setFormData] = useState<RegisterRequestDTO>(initialFormData);
  const { mutate, isLoading, isError, error, data } = useRegisterUser();
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.name.startsWith('address.')) {
      const addressField = e.target.name.split('.')[1];
      setFormData({
        ...formData,
        address: {
          ...formData.address,
          [addressField]: e.target.value,
        },
      });
    } else {
      setFormData({
        ...formData,
        [e.target.name]: e.target.value,
      });
    }
  };

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
