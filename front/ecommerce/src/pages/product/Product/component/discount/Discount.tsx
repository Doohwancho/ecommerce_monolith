import { DiscountDTO } from 'model'; // adjust the import path as needed

const Discount = ({ discounts }) => {
  return (
    <div className="discount-details">
      {discounts.map((discount, index) => (
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