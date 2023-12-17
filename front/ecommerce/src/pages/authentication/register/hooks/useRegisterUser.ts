import { useMutation, UseMutationResult } from 'react-query';
import { RegisterRequestDTO } from '../../../../../models/src/model/register-request-dto';
import { RegisterResponseDTO } from '../../../../../models/src/model/register-response-dto';

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

export default useRegisterUser;
