import { DiscountProps, DiscountDTO } from '../../types/Product.types';

const Discount: React.FC<DiscountProps> = ({ discounts }) => {
  return (
    <div className="discount-details">
      {discounts.map((discount: DiscountDTO, index: number) => (
        <div key={index}>
          <p>Discount Info</p>
          <p>Type: {discount.discountType}</p>
          <p>Value: {discount.discountValue}</p>
          <p>Start Date: {discount.startDate}</p>
          <p>End Date: {discount.endDate}</p>
        </div>
      ))}
    </div>
  );
};

export default Discount;