import styled from 'styled-components'

const Wrapper = styled.main`
  .product-center {
    display: grid;
    gap: 4rem;
    margin-top: 2rem;

    @media (min-width: 992px) {
      grid-template-columns: 1fr 1fr;
      align-items: start;
    }
  }

  .content {
    background: #fff;
    padding: 2rem;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);

    h2 {
      color: var(--clr-primary-5);
      font-size: 2rem;
      margin-bottom: 1rem;
    }

    .price {
      color: var(--clr-primary-6);
      font-size: 1.5rem;
      font-weight: bold;
      margin-bottom: 1rem;
    }

    .info {
      display: flex;
      flex-direction: column;
      margin-bottom: 1rem;
      span {
        font-weight: bold;
      }
    }

    button {
      background: var(--clr-primary-5, #007bff); // Fallback color if variable not defined
      color: white;
      border: 1px solid transparent;
      padding: 0.75rem 1.5rem;
      border-radius: 5px;
      cursor: pointer;
      margin-right: 1rem;
      transition: background-color 0.3s ease;

      &:hover {
        background-color: var(--clr-primary-7, #0056b3); // Fallback color if variable not defined
      }

      &:not(:last-child) {
        margin-bottom: 1rem;
      }

      // Ensure visibility
      visibility: visible;
      opacity: 1;
    }

    .discount-details {
      margin-top: 1rem;
      padding: 1rem;
      background-color: #f7f7f7;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

      p {
        margin: 0.5rem 0;
        font-size: 0.9rem;
        color: #333;

        &:first-child {
          margin-top: 0;
        }
      }
    }
  }

  // Style for the empty line at the end
  .empty-line {
    height: 1rem; // Adjust the height as needed to create the desired space
  }

  hr {
    margin: 2rem 0;
  }

  @media (min-width: 992px) {
    .price {
      font-size: 1.75rem;
    }
  }
`
export {Wrapper};