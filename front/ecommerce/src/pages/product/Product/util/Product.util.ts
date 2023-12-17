export const isValidProductId = (productId: string | undefined): productId is string => {
    return !!productId && !isNaN(parseInt(productId, 10));
  };