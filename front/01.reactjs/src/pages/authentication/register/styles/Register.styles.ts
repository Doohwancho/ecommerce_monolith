import styled from 'styled-components';

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


export { FormContainer, FormTitle, Form, FormGroup, Label, Input, Button, ErrorMessage, LoadingMessage, SuccessMessage };