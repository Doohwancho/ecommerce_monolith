import React from 'react';
import styled from 'styled-components';
import { AllCategoriesByDepthResponseDTO } from 'model';
import { Link } from 'react-router-dom';


interface ModalProps {
  setMenModalOn: (value: boolean) => void
  categories: AllCategoriesByDepthResponseDTO[]
}

const groupLowCategories = (categories: AllCategoriesByDepthResponseDTO[]) => {
    const grouped = {};

    categories.forEach(category => {
      const midId = category.midCategoryId;
      if (!grouped[midId]) {
        grouped[midId] = {
          midCategoryName: category.midCategoryName,
          lowCategories: []
        };
      }

      grouped[midId].lowCategories.push({
        id: category.lowCategoryId,
        name: category.lowCategoryName
      });
    });

    return Object.values(grouped);
  }

const Modal: React.FC<ModalProps> = ({ setMenModalOn, categories }) => {
  return (
    
    <ModalWrapper>
        <div className="modal">
            <div className="modalWrapper" onMouseLeave={() => setMenModalOn(false)}>
            {groupLowCategories(categories).map((group, index) => (
                <ul className="modalCol" key={index}>
                    <li className="cateTitle">{group.midCategoryName}</li>
                    <ul className="cateList">
                        {group.lowCategories.map((lowCategory, subIndex) => (
                            <li key={subIndex}>
                                <Link to={`/products/category/${lowCategory.id}`}>
                                    {lowCategory.name}
                                </Link>
                            </li>
                        ))}
                    </ul>
                </ul>
            ))}
            </div>
        </div>
    </ModalWrapper>
  );
}

const ModalWrapper = styled.div`
font-family: ${props => props.theme.fontContent};

a {
  text-decoration: none;
  cursor: pointer;
}

.modal {
  position: fixed;
  top: 80px;
  right: 0;
  bottom: 0;
  left: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 100;
  animation-duration: 0.5s;
  animation-name: slidein;

  .modalWrapper {
    position: relative;
    width: 100%;
    background-color: white;
    display: flex;
    justify-content: center;

    .modalCol {
      padding: 40px;

      .cateTitle {
        margin-bottom: 15px;
        font-weight: 700;
        font-size: 18px;
        color: black;
      }

      .cateList {
        margin-top: 0;

        a {
            color: black;
            text-decoration: none;
        }

        li {
          margin-bottom: 7px;
          font-size: 14px;
          color: gray;
        }
      }
    }
  }
}

@keyframes slidein {
  from {
    top: 0;
  }

  to {
    top: 80px;
  }
}
`;

export default Modal;
