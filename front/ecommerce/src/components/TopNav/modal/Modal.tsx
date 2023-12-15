import React from 'react';
import { ModalWrapper } from './styles/Modal.styles'
import { Link } from 'react-router-dom';
import { ModalProps, CategoryGroupProps, LowCategory } from './type/modalTypes';
import { groupLowCategories } from './util/modalUtil'

const CategoryGroup: React.FC<{ group: CategoryGroupProps }> = ({ group }) => (
  <ul className="modalCol">
    <li className="cateTitle">{group.midCategoryName}</li>
    <ul className="cateList">
      {group.lowCategories.map((lowCategory: LowCategory, subIndex: number) => (
        <li key={subIndex}>
          <Link to={`/products/category/${lowCategory.id}`}>
            {lowCategory.name}
          </Link>
        </li>
      ))}
    </ul>
  </ul>
);

const Modal: React.FC<ModalProps> = ({ setMenModalOn, categories }) => (
  <ModalWrapper>
    <div className="modal">
      <div className="modalWrapper" onMouseLeave={() => setMenModalOn(false)}>
        {groupLowCategories(categories).map((group, index) => (
          <CategoryGroup key={index} group={group} />
        ))}
      </div>
    </div>
  </ModalWrapper>
);


export default Modal;
