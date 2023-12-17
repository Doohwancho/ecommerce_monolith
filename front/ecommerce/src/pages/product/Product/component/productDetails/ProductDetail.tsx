import Discount from '../discount/Discount';
import { ProductDetailProps } from '../../types/Product.types';


const ProductDetail: React.FC<ProductDetailProps> = ({ product, chosenOption, onOptionChange }) => {
  
  return (
    <section className='content'>
      <h2>{product.name}</h2>

      <div className='info'>
        <span>Description: </span>
        {product.description}
      </div>

      <div className='info'>
        <span>Category : </span>
        {product.categoryName}
      </div>

      <div className='info'>
        <span>Options : </span>
        <div>
          {Object.keys(product).map(key => {
            if (typeof product[key] === 'object') {
              return (
                <button key={key} onClick={() => onOptionChange(key)}>
                  {key}
                </button>
              );
            }
            return null;
          })}
        </div>
      </div>

      {chosenOption && (
        <>
          <p>Price: {chosenOption.price}</p>
          <p>Quantity: {chosenOption.quantity}</p>
          {chosenOption.discounts && chosenOption.discounts.length > 0 ? (
            <Discount discounts={chosenOption.discounts} />
          ) : (
            <p>No discounts available for this option.</p>
          )}
        </>
      )}
    </section>
  );
};

export default ProductDetail;
