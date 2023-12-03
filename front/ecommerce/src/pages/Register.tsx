import React, { useState } from 'react';
import { useMutation, UseMutationResult } from 'react-query';

import { RegisterRequestDTO, RegisterResponseDTO } from 'model';

const initialFormData = {
    username: '',
    email: '',
    password: '',
    name: '',
    address: {
      street: '',
      city: '',
      state: '',
      country: '',
      zipCode: '',
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
        },
        onError: (error) => {
          console.error('Registration Error:', error);
        }
    });
  };

  return (
    <div>
      <h2>Register</h2>
      {
        <form onSubmit={handleSubmit}>
            <div>
            <label htmlFor="username">Username:</label>
            <input
                type="text"
                id="username"
                name="username"
                value={formData.username}
                onChange={handleChange}
            />
            </div>
            <div>
            <label htmlFor="email">Email:</label>
            <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
            />
            </div>
            <div>
            <label htmlFor="password">Password:</label>
            <input
                type="password"
                id="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
            />
            </div>
            <div>
            <label htmlFor="name">Name:</label>
            <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
            />
            </div>
            {/* Address Fields */}
            <div>
            <label htmlFor="street">Street:</label>
            <input
                type="text"
                id="street"
                name="address.street"
                value={formData.address.street}
                onChange={handleChange}
            />
            </div>
            <div>
            <label htmlFor="city">City:</label>
            <input
                type="text"
                id="city"
                name="address.city"
                value={formData.address.city}
                onChange={handleChange}
            />
            </div>
            <div>
            <label htmlFor="state">State:</label>
            <input
                type="text"
                id="state"
                name="address.state"
                value={formData.address.state}
                onChange={handleChange}
            />
            </div>
            <div>
            <label htmlFor="country">Country:</label>
            <input
                type="text"
                id="country"
                name="address.country"
                value={formData.address.country}
                onChange={handleChange}
            />
            </div>
            <div>
            <label htmlFor="zipCode">Zipcode:</label>
            <input
                type="text"
                id="zipCode"
                name="address.zipCode"
                value={formData.address.zipCode}
                onChange={handleChange}
            />
            </div>
            <button type="submit">Register</button>
        </form>
    }
    {isLoading && <p>Registering...</p>}
    {isError && <p>Error: {error.message}</p>}
    {data && <p>Registration Successful!</p>}
    </div>
  );
};

export default Register;
